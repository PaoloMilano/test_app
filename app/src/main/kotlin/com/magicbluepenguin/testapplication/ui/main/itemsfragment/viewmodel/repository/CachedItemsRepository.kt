package com.magicbluepenguin.testapplication.ui.main.itemsfragment.viewmodel.repository

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.magicbluepenguin.testapplication.data.cache.ItemDao
import com.magicbluepenguin.testapplication.data.models.Item
import com.magicbluepenguin.testapplication.data.network.ItemService
import com.magicbluepenguin.testapplication.util.IsFetchingMoreItems
import com.magicbluepenguin.testapplication.util.RefreshInProgress
import com.magicbluepenguin.testapplication.util.RepositoryState

class CachedItemsRepository(
    val itemService: ItemService,
    val itemDao: ItemDao,
    val pageSize: Int = 10
) : ItemsRepository {
    var repositoryStatelistener: ((RepositoryState) -> Unit)? = null

    override fun connect(): LiveData<PagedList<Item>> {
        val factory: DataSource.Factory<Int, Item> = itemDao.getAllItemsPaged()
        return LivePagedListBuilder<Int, Item>(
            factory,
            pageSize
        ).build()
    }

    override suspend fun refresh() {
        repositoryStatelistener?.invoke(RefreshInProgress(true))

        val freshItems = itemService.listItems()

        if (!freshItems.isEmpty()) {
            itemDao.insertAll(freshItems)
            // Only clear out the old items if the request succeeds. This way we can ensure we can still
            // show the user some data even when the server can't be reached
            itemDao.deleteAllExcluding(freshItems.map { it._id })
        }
        repositoryStatelistener?.invoke(RefreshInProgress(false))
    }

    override suspend fun fetchNextItems() {
        repositoryStatelistener?.invoke(IsFetchingMoreItems(true))
        itemDao.insertAll(itemService.listItems(itemDao.getHighestId()))
        repositoryStatelistener?.invoke(IsFetchingMoreItems(false))
    }

    override fun setOnRepositoryStateListener(listener: (RepositoryState) -> Unit) {
        repositoryStatelistener = listener
    }
}