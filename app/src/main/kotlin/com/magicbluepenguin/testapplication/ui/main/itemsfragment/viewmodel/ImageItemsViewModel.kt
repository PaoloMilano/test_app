package com.magicbluepenguin.testapplication.ui.main.itemsfragment.viewmodel

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.magicbluepenguin.testapplication.data.cache.ImageItemDao
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

    val isFetchingMoreRecentItems = MutableLiveData<Boolean>()
    val isFetchingMoreOlderItems = MutableLiveData<Boolean>()
    val isRefreshing = MutableLiveData<Boolean>()
    val networkError = MutableLiveData<NetworkError>()

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
    }

    fun connectToDataStream(listener: (LiveData<PagedList<ImageItem>>) -> Unit) =
        viewModelScope.launch {
            refresh()
            listener.invoke(itemsRepository.connect())
        }

    companion object {

        // This removes clutter from the activity and puts the initialisation code in a
        // single place that is easy to find and debug
        fun getInstanceWithCahedRepository(
            activityContext: AppCompatActivity,
            imageItemDao: ImageItemDao,
            authHeader: String,
            certPin: String
        ): ImageItemsViewModel {
            return ViewModelProvider(activityContext,
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                        val itemsRepository =
                            CachedImageItemsRepository(
                                RetrofitServiceProvider(authHeader, certPin).itemService,
                                imageItemDao
                            )
                        return ImageItemsViewModel(itemsRepository) as T
                    }
                }).get(ImageItemsViewModel::class.java)
        }
    }
}
