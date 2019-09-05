package com.magicbluepenguin.testapplication.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.magicbluepenguin.testapplication.data.cache.ItemDao
import com.magicbluepenguin.testapplication.data.models.Item
import com.magicbluepenguin.testapplication.data.network.RetrofitServiceProvider
import com.magicbluepenguin.testapplication.util.IsFetchingMoreItems
import com.magicbluepenguin.testapplication.util.RefreshInProgress
import com.magicbluepenguin.testapplication.util.RepositoryState

class ItemsRepository(
    val retrofitServiceProvider: RetrofitServiceProvider,
    val itemDao: ItemDao,
    val batchSize: Int = 10
) {
    var repositoryStatelistener: ((RepositoryState) -> Unit)? = null

    fun connect(): LiveData<PagedList<Item>> {
        val factory: DataSource.Factory<Int, Item> = itemDao.getAllItemsPaged()

        return LivePagedListBuilder<Int, Item>(
            factory,
            50
        ).build()
    }

    suspend fun refresh() {
        repositoryStatelistener?.invoke(RefreshInProgress(true))

        val freshItems = retrofitServiceProvider.itemService.listItems()

        // Only clear out the old items if the request succeeds. This way we can ensure we can still
        // show the user some data even when the server can't be reached
        if (!freshItems.isEmpty()) {
            itemDao.deleteAll(itemDao.getAllItems().map { it.img })
            itemDao.insertAll(freshItems)
        }

        repositoryStatelistener?.invoke(RefreshInProgress(false))
    }

    suspend fun fetchNextItems() {
        repositoryStatelistener?.invoke(IsFetchingMoreItems(true))
        val items = retrofitServiceProvider.itemService.listItems()
        itemDao.insertAll(items)
        repositoryStatelistener?.invoke(IsFetchingMoreItems(false))
    }

    fun setOnRepositoryStateListener(listener: (RepositoryState) -> Unit) {
        repositoryStatelistener = listener
    }
}