package com.magicbluepenguin.testapplication.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.magicbluepenguin.testapplication.data.cache.AppDatabase
import com.magicbluepenguin.testapplication.data.models.Item
import com.magicbluepenguin.testapplication.data.network.RetrofitServiceProvider
import com.magicbluepenguin.testapplication.util.HasMoreItems
import com.magicbluepenguin.testapplication.util.NetworkOperationInProgress
import com.magicbluepenguin.testapplication.util.RepositoryState

class ItemsRepository(
    val retrofitServiceProvider: RetrofitServiceProvider,
    val appDatabase: AppDatabase
) {
    var repositoryStatelistener: ((RepositoryState) -> Unit)? = null

    fun connect(): LiveData<PagedList<Item>> {
        val factory: DataSource.Factory<Int, Item> = appDatabase.itemDao().getAllItems()

        return LivePagedListBuilder<Int, Item>(
            factory,
            50
        ).build()
    }

    fun setOnRepositoryStateListener(listener: (RepositoryState) -> Unit) {
        repositoryStatelistener = listener
    }

    suspend fun fetchNextItems(batchSize: Int) {
        repositoryStatelistener?.invoke(NetworkOperationInProgress(true))

        val items = retrofitServiceProvider.itemService.listItems()
        if (items.size < batchSize) {
            repositoryStatelistener?.invoke(HasMoreItems(false))
        } else {
            repositoryStatelistener?.invoke(HasMoreItems(true))
        }
        appDatabase.itemDao().insertAll(items)

        repositoryStatelistener?.invoke(NetworkOperationInProgress(false))
    }
}