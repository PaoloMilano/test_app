package com.magicbluepenguin.testapplication

import com.magicbluepenguin.testapplication.data.cache.ImageItemDao
import com.magicbluepenguin.testapplication.data.network.ItemService
import com.magicbluepenguin.testapplication.ui.main.itemsfragment.viewmodel.repository.CachedImageItemsRepository
import com.magicbluepenguin.testapplication.util.GenericNetworkError
import com.magicbluepenguin.testapplication.util.IsFetchingMoreOlderItems
import com.magicbluepenguin.testapplication.util.IsFetchingMoreRecentItems
import com.magicbluepenguin.testapplication.util.NetworkUnavailableError
import com.magicbluepenguin.testapplication.util.RefreshInProgress
import com.magicbluepenguin.testapplication.util.RepositoryState
import com.magicbluepenguin.testapplication.util.UnsecureConnectionError
import dummyItems
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import java.net.SocketException
import javax.net.ssl.SSLException

class CachedImageItemRepositoryTest {

    val mockItemDao = mockk<ImageItemDao> {
        every { runBlocking { insertAll(any()) } } answers { Unit }
        every { runBlocking { deleteAllExcluding(any()) } } answers { 0 }
        every { runBlocking { getMostRecentId() } } answers { "" }
        every { runBlocking { getOldestId() } } answers { "" }
    }
    val mockItemService = mockk<ItemService> {
        every { runBlocking { listItems(any(), any()) } }.answers { emptyList() }
    }

    private var _itemRepository: CachedImageItemsRepository? = null
    private val itemRepository: CachedImageItemsRepository
        get() = _itemRepository!!

    @Before
    fun setUp() {
        _itemRepository =
            CachedImageItemsRepository(
                mockItemService,
                mockItemDao
            )
    }

    @Test
    fun `test state updates on refresh`() = runBlocking {
        val repositoryStateCatcher = mutableListOf<RepositoryState>()
        itemRepository.setOnRepositoryStateListener { repositoryStateCatcher.add(it) }
        itemRepository.refresh()

        assertEquals(
            repositoryStateCatcher,
            listOf(RefreshInProgress(true), RefreshInProgress(false))
        )
    }

    @Test
    fun `test item inserts on refresh`() = runBlocking {
        val repositoryStateCatcher = mutableListOf<RepositoryState>()
        itemRepository.setOnRepositoryStateListener { repositoryStateCatcher.add(it) }
        itemRepository.refresh()

        assertEquals(
            repositoryStateCatcher,
            listOf(RefreshInProgress(true), RefreshInProgress(false))
        )
    }

    @Test
    fun `test item inserts on fetching older items`() = runBlocking {
        every { mockItemDao.getOldestId() } answers { "" }
        val repositoryStateCatcher = mutableListOf<RepositoryState>()
        itemRepository.setOnRepositoryStateListener { repositoryStateCatcher.add(it) }
        itemRepository.fetchOlderItems()

        assertEquals(
            repositoryStateCatcher,
            listOf(IsFetchingMoreOlderItems(true), IsFetchingMoreOlderItems(false))
        )
    }

    @Test
    fun `test item inserts on fetching newer items`() = runBlocking {
        every { mockItemDao.getMostRecentId() } answers { "" }
        val repositoryStateCatcher = mutableListOf<RepositoryState>()
        itemRepository.setOnRepositoryStateListener { repositoryStateCatcher.add(it) }
        itemRepository.fetchNewerItems()

        assertEquals(
            repositoryStateCatcher,
            listOf(IsFetchingMoreRecentItems(true), IsFetchingMoreRecentItems(false))
        )
    }

    @Test
    fun `test item updates on refresh`() = runBlocking {
        every { runBlocking { mockItemService.listItems() } }.answers { dummyItems }
        itemRepository.refresh()
        verify { mockItemDao.insertAll(dummyItems) }
        verify { mockItemDao.deleteAllExcluding(dummyItems.map { it._id }) }
    }

    @Test
    fun `test item inserts on failed fetch`() = runBlocking {
        every { runBlocking { mockItemService.listItems() } }.answers { dummyItems }
        every { runBlocking { mockItemDao.insertAll(any()) } }.answers { Unit }

        itemRepository.refresh()

        // Check that if the network does not return any item we leave the old ones there
        verify { mockItemDao.insertAll(dummyItems) }
    }

    @Test
    fun `test generic error handling with state update`() = runBlocking {
        val repositoryStateCatcher = mutableListOf<RepositoryState>()
        itemRepository.setOnRepositoryStateListener { repositoryStateCatcher.add(it) }

        `test network error handling` { runBlocking { itemRepository.refresh() } }
        `test network error handling` { runBlocking { itemRepository.fetchNewerItems() } }
        `test network error handling` { runBlocking { itemRepository.fetchOlderItems() } }

        assertEquals(
            (0..2).map { GenericNetworkError },
            repositoryStateCatcher.filterIsInstance(GenericNetworkError::class.java)
        )
    }

    @Test
    fun `test network unavailable handling with state update`() = runBlocking {
        val repositoryStateCatcher = mutableListOf<RepositoryState>()
        itemRepository.setOnRepositoryStateListener { repositoryStateCatcher.add(it) }

        val networkError = SocketException()
        `test network error handling`(networkError) { runBlocking { itemRepository.refresh() } }
        `test network error handling`(networkError) { runBlocking { itemRepository.fetchNewerItems() } }
        `test network error handling`(networkError) { runBlocking { itemRepository.fetchOlderItems() } }

        assertEquals(
            (0..2).map { NetworkUnavailableError },
            repositoryStateCatcher.filterIsInstance(NetworkUnavailableError::class.java)
        )
    }

    @Test
    fun `test ssl error handling with state update`() = runBlocking {
        val repositoryStateCatcher = mutableListOf<RepositoryState>()
        itemRepository.setOnRepositoryStateListener { repositoryStateCatcher.add(it) }

        val networkError = SSLException("")
        `test network error handling`(networkError) { runBlocking { itemRepository.refresh() } }
        `test network error handling`(networkError) { runBlocking { itemRepository.fetchNewerItems() } }
        `test network error handling`(networkError) { runBlocking { itemRepository.fetchOlderItems() } }

        assertEquals(
            (0..2).map { UnsecureConnectionError },
            repositoryStateCatcher.filterIsInstance(NetworkUnavailableError::class.java)
        )
    }

    private fun `test network error handling`(exception: Exception = HttpException(mockk(relaxed = true)), call: () -> Unit) {
        try {
            every { runBlocking { mockItemService.listItems(any(), any()) } } throws exception
            call.invoke()
        } catch (ex: HttpException) {
            fail("Exception unhandled from network call")
        }
    }
}
