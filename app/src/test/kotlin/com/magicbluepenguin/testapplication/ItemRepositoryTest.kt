package com.magicbluepenguin.testapplication

import com.magicbluepenguin.testapplication.data.cache.ItemDao
import com.magicbluepenguin.testapplication.data.network.ItemService
import com.magicbluepenguin.testapplication.ui.main.itemsfragment.viewmodel.repository.CachedItemsRepository
import com.magicbluepenguin.testapplication.util.IsFetchingMoreItems
import com.magicbluepenguin.testapplication.util.RefreshInProgress
import com.magicbluepenguin.testapplication.util.RepositoryState
import dummyItems
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ItemRepositoryTest {

    val mockItemDao = mockk<ItemDao> {
        every { runBlocking { insertAll(any()) } }.answers { Unit }
        every { runBlocking { deleteAllExcluding(any()) } }.answers { 0 }
    }
    val mockItemService = mockk<ItemService> {
        every { runBlocking { listItems() } }.answers { emptyList() }
    }

    private var _itemRepository: CachedItemsRepository? = null
    private val itemRepository: CachedItemsRepository
        get() = _itemRepository!!

    @Before
    fun setUp() {
        _itemRepository =
            CachedItemsRepository(
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
    fun `test item inserts on fetching next`() = runBlocking {
        val repositoryStateCatcher = mutableListOf<RepositoryState>()
        itemRepository.setOnRepositoryStateListener { repositoryStateCatcher.add(it) }
        itemRepository.fetchNextItems()

        assertEquals(
            repositoryStateCatcher,
            listOf(IsFetchingMoreItems(true), IsFetchingMoreItems(false))
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
    fun `test item inserts on fetch`() = runBlocking {
        every { runBlocking { mockItemService.listItems() } }.answers { dummyItems }
        every { runBlocking { mockItemDao.insertAll(any()) } }.answers { Unit }

        itemRepository.fetchNextItems()

        verify { mockItemDao.insertAll(dummyItems) }
    }
}
