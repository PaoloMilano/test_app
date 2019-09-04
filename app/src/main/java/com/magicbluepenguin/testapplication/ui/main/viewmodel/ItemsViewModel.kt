package com.magicbluepenguin.testapplication.ui.main.viewmodel

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import androidx.room.Room
import com.magicbluepenguin.testapplication.data.cache.AppDatabase
import com.magicbluepenguin.testapplication.data.models.Item
import com.magicbluepenguin.testapplication.data.network.RetrofitServiceProvider
import com.magicbluepenguin.testapplication.util.HasMoreItems
import com.magicbluepenguin.testapplication.util.NetworkError
import com.magicbluepenguin.testapplication.util.NetworkOperationInProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ItemsViewModel(val itemsRepository: ItemsRepository) : ViewModel() {

    val itemsLiveData: LiveData<PagedList<Item>>
    val isLoading = MutableLiveData<Boolean>()
    val hasMoreItems = MutableLiveData<Boolean>()

    companion object {

        // This removes clutter from the activity and puts the initialisation code in a
        // single place that is easy to find and debug
        fun getInstance(activityContext: AppCompatActivity, authHeader: String): ItemsViewModel {
            return ViewModelProvider(activityContext,
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

                        val itemsRepository =
                            ItemsRepository(
                                RetrofitServiceProvider(authHeader),
                                Room.databaseBuilder(
                                    activityContext,
                                    AppDatabase::class.java, "items_app_database"
                                ).build()
                            )
                        return ItemsViewModel(itemsRepository) as T
                    }
                }).get(ItemsViewModel::class.java)
        }
    }

    init {
        itemsLiveData = itemsRepository.connect()
        itemsRepository.setOnRepositoryStateListener { repositoryState ->
            viewModelScope.launch {
                // Use return value to force when statement to be exhaustive
                val ignore = when (repositoryState) {
                    is NetworkOperationInProgress -> isLoading.value = repositoryState.value
                    is HasMoreItems -> hasMoreItems.value = repositoryState.value
                    NetworkError -> TODO()
                }
            }
        }
    }

    fun fetchNextItems(batchSize: Int = 10) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                itemsRepository.fetchNextItems(batchSize)
            }
        }
    }
}
