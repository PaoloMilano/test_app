package com.magicbluepenguin.testapplication.ui.main.itemsfragment.viewmodel.repository

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.magicbluepenguin.testapplication.data.cache.ImageItemDao
import com.magicbluepenguin.testapplication.data.models.ImageItem
import com.magicbluepenguin.testapplication.data.network.ItemService
import com.magicbluepenguin.testapplication.util.GenericNetworkError
import com.magicbluepenguin.testapplication.util.IsFetchingMoreOlderItems
import com.magicbluepenguin.testapplication.util.IsFetchingMoreRecentItems
import com.magicbluepenguin.testapplication.util.NetworkUnavailableError
import com.magicbluepenguin.testapplication.util.RefreshInProgress
import com.magicbluepenguin.testapplication.util.RepositoryState
import com.magicbluepenguin.testapplication.util.UnsecureConnectionError
import java.net.SocketException
import javax.net.ssl.SSLException

class CachedImageItemsRepository(
    val itemService: ItemService,
    val itemDao: ImageItemDao,
    val pageSize: Int = 10
) : ImageItemsRepository {
    var repositoryStatelistener: ((RepositoryState) -> Unit)? = null

    override fun connect(): LiveData<PagedList<ImageItem>> {
        val factory: DataSource.Factory<Int, ImageItem> = itemDao.getAllItemsPaged()
        return LivePagedListBuilder<Int, ImageItem>(
            factory,
            pageSize
        ).build()
    }

    override suspend fun refresh() {
        repositoryStatelistener?.invoke(RefreshInProgress(true))

        val freshItems = listItems()

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
        val items = listItems(untilId = itemDao.getOldestId())
        repositoryStatelistener?.invoke(IsFetchingMoreOlderItems(false))
        itemDao.insertAll(items)
    }

    override suspend fun fetchNewerItems() {
        repositoryStatelistener?.invoke(IsFetchingMoreRecentItems(true))
        val items = listItems(fromId = itemDao.getMostRecentId())
        repositoryStatelistener?.invoke(IsFetchingMoreRecentItems(false))
        itemDao.insertAll(items)
    }

    private suspend fun listItems(
        fromId: String? = null,
        untilId: String? = null
    ): List<ImageItem> {
        try {
            return itemService.listItems(fromId = fromId, untilId = untilId)
        } catch (ex: Exception) {
            when (ex) {
                is SocketException -> repositoryStatelistener?.invoke(NetworkUnavailableError)
                is SSLException -> repositoryStatelistener?.invoke(UnsecureConnectionError)
                else -> repositoryStatelistener?.invoke(GenericNetworkError)
            }
        }
        return emptyList()
    }

    override fun setOnRepositoryStateListener(listener: (RepositoryState) -> Unit) {
        repositoryStatelistener = listener
    }
}