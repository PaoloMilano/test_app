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
import com.magicbluepenguin.testapplication.data.models.Item
import com.magicbluepenguin.testapplication.data.network.RetrofitServiceProvider
import com.magicbluepenguin.testapplication.ui.main.itemsfragment.viewmodel.repository.CachedItemsRepository
import com.magicbluepenguin.testapplication.ui.main.itemsfragment.viewmodel.repository.ItemsRepository
import com.magicbluepenguin.testapplication.util.IsFetchingMoreOlderItems
import com.magicbluepenguin.testapplication.util.IsFetchingMoreRecentItems
import com.magicbluepenguin.testapplication.util.NetworkError
import com.magicbluepenguin.testapplication.util.RefreshInProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ItemsViewModel(val itemsRepository: ItemsRepository) : ViewModel() {

    var itemsLiveData: LiveData<PagedList<Item>>? = null
    val isFetchingMoreRecentItems = MutableLiveData<Boolean>()
    val isFetchingMoreOlderItems = MutableLiveData<Boolean>()
    val isRefreshing = MutableLiveData<Boolean>()

    private var onLiveDataReadyListener: ((LiveData<PagedList<Item>>) -> Unit)? = null

    init {
        // Start by listening for state changes
        itemsRepository.setOnRepositoryStateListener { repositoryState ->
            viewModelScope.launch {

                // Use return value to force `when` statement to be exhaustive
                val ignore = when (repositoryState) {
                    is IsFetchingMoreOlderItems -> isFetchingMoreOlderItems.postValue(repositoryState.value)
                    is IsFetchingMoreRecentItems -> isFetchingMoreRecentItems.postValue(repositoryState.value)
                    is RefreshInProgress -> {
                        isRefreshing.postValue(repositoryState.value)
                    }
                    NetworkError -> TODO()
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

        if (itemsLiveData == null) {

            // Connect to the stream after refreshing so we show fresh latest available data
            itemsRepository.connect().let {
                itemsLiveData = it
                if (onLiveDataReadyListener != null) {
                    onLiveDataReadyListener?.invoke(it)
                }
            }
        }
    }

    fun onDataStreamReadyListener(listener: (LiveData<PagedList<Item>>) -> Unit) {
        onLiveDataReadyListener = listener
        itemsLiveData?.let(listener)
    }

    companion object {

        // This removes clutter from the activity and puts the initialisation code in a
        // single place that is easy to find and debug
        fun getInstanceWithCahedRepository(
            activityContext: AppCompatActivity,
            authHeader: String
        ): ItemsViewModel {
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
                            CachedItemsRepository(
                                RetrofitServiceProvider(authHeader).itemService,
                                db.itemDao()
                            )
                        return ItemsViewModel(itemsRepository) as T
                    }
                }).get(ItemsViewModel::class.java)
        }
    }
}
