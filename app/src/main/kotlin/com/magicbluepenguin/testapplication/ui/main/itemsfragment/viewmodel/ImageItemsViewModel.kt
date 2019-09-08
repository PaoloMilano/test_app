package com.magicbluepenguin.testapplication.ui.main.itemsfragment.viewmodel

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import androidx.room.Room
import com.magicbluepenguin.testapplication.data.cache.AppDatabase
import com.magicbluepenguin.testapplication.data.models.ImageItem
import com.magicbluepenguin.testapplication.data.network.RetrofitServiceProvider
import com.magicbluepenguin.testapplication.ui.main.itemsfragment.viewmodel.repository.CachedImageItemsRepository
import com.magicbluepenguin.testapplication.ui.main.itemsfragment.viewmodel.repository.ImageItemsRepository
import com.magicbluepenguin.testapplication.util.IsFetchingMoreOlderItems
import com.magicbluepenguin.testapplication.util.IsFetchingMoreRecentItems
import com.magicbluepenguin.testapplication.util.NetworkError
import com.magicbluepenguin.testapplication.util.RefreshInProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageItemsViewModel(val itemsRepository: ImageItemsRepository) : ViewModel() {

    var imageItemsLiveData: LiveData<PagedList<ImageItem>>? = null
    val isFetchingMoreRecentItems = MutableLiveData<Boolean>()
    val isFetchingMoreOlderItems = MutableLiveData<Boolean>()
    val isRefreshing = MutableLiveData<Boolean>()
    val networkError = MutableLiveData<NetworkError>()

    private var onLiveDataReadyListener: ((LiveData<PagedList<ImageItem>>) -> Unit)? = null

    init {
        // Start by listening for state changes
        itemsRepository.setOnRepositoryStateListener { repositoryState ->
            viewModelScope.launch {

                // Use return value to force `when` statement to be exhaustive
                val ignore = when (repositoryState) {
                    is IsFetchingMoreOlderItems -> isFetchingMoreOlderItems.postValue(
                        repositoryState.value
                    )
                    is IsFetchingMoreRecentItems -> isFetchingMoreRecentItems.postValue(
                        repositoryState.value
                    )
                    is RefreshInProgress -> {
                        isRefreshing.postValue(repositoryState.value)
                    }
                    is NetworkError -> networkError.postValue(repositoryState)
                }
            }
        }

        // Then ask for fresh data
        refresh()
    }

    fun fetchOlderItems() = viewModelScope.launch {
        withContext(Dispatchers.Default) {
            itemsRepository.fetchOlderItems()
        }
    }

    fun fetchNewerItems() = viewModelScope.launch {
        withContext(Dispatchers.Default) {
            itemsRepository.fetchNewerItems()
        }
    }

    fun refresh() = viewModelScope.launch {
        withContext(Dispatchers.Default) {
            itemsRepository.refresh()
        }

        if (imageItemsLiveData == null) {

            // Connect to the stream after refreshing so we show fresh latest available data
            itemsRepository.connect().let {
                imageItemsLiveData = it
                if (onLiveDataReadyListener != null) {
                    onLiveDataReadyListener?.invoke(it)
                }
            }
        }
    }

    fun onDataStreamReadyListener(listener: (LiveData<PagedList<ImageItem>>) -> Unit) {
        onLiveDataReadyListener = listener
        imageItemsLiveData?.let(listener)
    }

    companion object {

        // This removes clutter from the activity and puts the initialisation code in a
        // single place that is easy to find and debug
        fun getInstanceWithCahedRepository(
            activityContext: AppCompatActivity,
            authHeader: String,
            certPin: String
        ): ImageItemsViewModel {
            return ViewModelProvider(activityContext,
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                        val db = Room.databaseBuilder(
                            activityContext,
                            AppDatabase::class.java, "app_database"
                        ).build()

                        activityContext.lifecycle.addObserver(object : LifecycleObserver {
                            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                            fun onDestroy() {
                                db.close()
                            }
                        })

                        val itemsRepository =
                            CachedImageItemsRepository(
                                RetrofitServiceProvider(authHeader, certPin).itemService,
                                db.imageItemDao()
                            )
                        return ImageItemsViewModel(itemsRepository) as T
                    }
                }).get(ImageItemsViewModel::class.java)
        }
    }
}
