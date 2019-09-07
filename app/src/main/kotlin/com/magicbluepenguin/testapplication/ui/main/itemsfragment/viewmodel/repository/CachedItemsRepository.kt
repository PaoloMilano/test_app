package com.magicbluepenguin.testapplication.ui.main.itemsfragment.viewmodel.repository

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.magicbluepenguin.testapplication.data.cache.ItemDao
import com.magicbluepenguin.testapplication.data.models.Item
import com.magicbluepenguin.testapplication.data.network.ItemService
import com.magicbluepenguin.testapplication.util.IsFetchingMoreOlderItems
import com.magicbluepenguin.testapplication.util.IsFetchingMoreRecentItems
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

    override suspend fun fetchOlderItems() {
        repositoryStatelistener?.invoke(IsFetchingMoreOlderItems(true))
        itemDao.insertAll(itemService.listItems(untilId = itemDao.getOldestId()))
        repositoryStatelistener?.invoke(IsFetchingMoreOlderItems(false))
    }

    override suspend fun fetchNewerItems() {
        repositoryStatelistener?.invoke(IsFetchingMoreRecentItems(true))
        itemDao.insertAll(itemService.listItems(fromId = itemDao.getMostRecentId()))
        repositoryStatelistener?.invoke(IsFetchingMoreRecentItems(false))
    }

    override fun setOnRepositoryStateListener(listener: (RepositoryState) -> Unit) {
        repositoryStatelistener = listener
    }
}