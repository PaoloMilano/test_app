package com.magicbluepenguin.testapplication

import com.magicbluepenguin.testapplication.data.cache.ItemDao
import com.magicbluepenguin.testapplication.data.models.Item
import com.magicbluepenguin.testapplication.data.network.ItemService
import com.magicbluepenguin.testapplication.ui.main.viewmodel.ItemsRepository
import com.magicbluepenguin.testapplication.util.RefreshInProgress
import com.magicbluepenguin.testapplication.util.RepositoryState
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ItemRepositoryTest {

    val items = listOf(
        Item("image 1", "string in image 1", 1.01f),
        Item("image 2", "string in image 2", 2.02f),
        Item("image 3", "string in image 3", 3.03f),
        Item("image 4", "string in image 4", 4.04f)
    )

    val mockItemDao = mockk<ItemDao>()
    val mockItemService = mockk<ItemService>()

    private var _itemRepository: ItemsRepository? = null
    private val itemRepository: ItemsRepository
        get() = _itemRepository!!

    @Before
    fun setUp() {
        _itemRepository = ItemsRepository(mockItemService, mockItemDao)
    }

    @Test
    fun `test state updates on refresh`() = runBlocking {
        every { runBlocking { mockItemService.listItems() } }.answers { emptyList() }
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
        every { runBlocking { mockItemService.listItems() } }.answers { emptyList() }
        every { runBlocking { mockItemDao.insertAll(any()) } }.answers { Unit }
        val repositoryStateCatcher = mutableListOf<RepositoryState>()
        itemRepository.setOnRepositoryStateListener { repositoryStateCatcher.add(it) }
        itemRepository.refresh()

        assertEquals(
            repositoryStateCatcher,
            listOf(RefreshInProgress(true), RefreshInProgress(false))
        )
    }

    @Test
    fun `test item updates on refresh`() = runBlocking {
        every { runBlocking { mockItemService.listItems() } }.answers { items }
        every { runBlocking { mockItemDao.getAllItems() } }.answers { emptyList() }
        every { runBlocking { mockItemDao.insertAll(any()) } }.answers { Unit }

        every { runBlocking { mockItemDao.deleteAllExcluding(any()) } }.answers { items.size }

        itemRepository.refresh()

        verify { mockItemDao.insertAll(items) }
        verify { mockItemDao.deleteAllExcluding(items.map { it.img }) }
    }

    @Test
    fun `test item inserts on fetch`() = runBlocking {
        every { runBlocking { mockItemService.listItems() } }.answers { items }
        every { runBlocking { mockItemDao.insertAll(any()) } }.answers { Unit }

        itemRepository.fetchNextItems()

        verify { mockItemDao.insertAll(items) }
    }
}
