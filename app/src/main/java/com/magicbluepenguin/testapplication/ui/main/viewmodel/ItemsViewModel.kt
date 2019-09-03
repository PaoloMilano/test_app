package com.magicbluepenguin.testapplication.ui.main.viewmodel

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.magicbluepenguin.testapplication.data.cache.AppDatabase
import com.magicbluepenguin.testapplication.data.network.RetrofitServiceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ItemsViewModel(val itemsRepository: ItemsRepository) : ViewModel() {

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

    fun fetchNextItems() {
        viewModelScope.launch {

            val itemResponse = withContext(Dispatchers.Default) {
                itemsRepository.fetchNextItems()
            }

            val error = itemResponse.error
            val hasMore = itemResponse.hasMore
            val items = itemResponse.items
        }
    }
}
