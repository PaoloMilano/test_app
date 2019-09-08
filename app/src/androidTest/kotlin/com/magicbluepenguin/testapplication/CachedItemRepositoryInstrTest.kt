package com.magicbluepenguin.testapplication

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.magicbluepenguin.testapplication.data.cache.ItemDao
import com.magicbluepenguin.testapplication.data.models.Item
import com.magicbluepenguin.testapplication.data.network.ItemService
import com.magicbluepenguin.testapplication.ui.main.itemsfragment.viewmodel.repository.CachedItemsRepository
import dummyItems
import getInMemoryDb
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * A class to test the parts of the Cache repository that depend on DB implementation
 */
@RunWith(AndroidJUnit4::class)
class CachedItemRepositoryInstrTest {

    private val mockItemService = mockk<ItemService> {
        every { runBlocking { listItems(any(), any()) } } answers { emptyList() }
    }
    private var _itemRepository: CachedItemsRepository? = null
    private val itemRepository: CachedItemsRepository
        get() = _itemRepository!!

    private var _itemDao: ItemDao? = null
    private val itemDao: ItemDao
        get() = _itemDao!!

    @Before
    fun setUp() {
        _itemDao = getInMemoryDb(InstrumentationRegistry.getInstrumentation().context).itemDao()
        _itemRepository =
            CachedItemsRepository(
                mockItemService,
                itemDao
            )
    }

    @Test
    fun testConnection() {
        itemDao.insertAll(dummyItems)
        val latch = CountDownLatch(1)
        val itemsCatcher = mutableListOf<Item>()
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            itemRepository.connect().observeForever {
                it?.let {
                    itemsCatcher.addAll(it)
                }
                latch.countDown()
            }
        }

        // We don't want this test to hang forever in case of failure so give it a time limit
        latch.await(2, TimeUnit.SECONDS)
        assertEquals(dummyItems.sortedBy { it._id }.reversed(), itemsCatcher)
    }

    @Test
    fun testUpdates() {
        val latch = CountDownLatch(1)
        val itemsCatcher = mutableListOf<Item>()
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            itemRepository.connect().observeForever {
                it?.let {
                    itemsCatcher.addAll(it)
                }
                latch.countDown()
            }
        }
        itemDao.insertAll(dummyItems)

        // We don't want this test to hang forever in case of failure so give it a time limit
        latch.await(2, TimeUnit.SECONDS)
        latch.await()
        assertEquals(dummyItems.sortedBy { it._id }.reversed(), itemsCatcher)
    }

    @Test
    fun testFetchingFirstBatch() = runBlocking {
        every { runBlocking { mockItemService.listItems(any()) } } answers { emptyList() }
        itemRepository.refresh()
        verify { runBlocking { mockItemService.listItems() } }
    }

    @Test
    fun testFetchingOlderBatch() = runBlocking {
        every { runBlocking { mockItemService.listItems(any()) } } answers { emptyList() }
        itemDao.insertAll(dummyItems)
        itemRepository.fetchOlderItems()
        verify { runBlocking { mockItemService.listItems(untilId = itemDao.getOldestId()) } }
    }

    @Test
    fun testFetchingNewerBatch() = runBlocking {
        every { runBlocking { mockItemService.listItems(any()) } } answers { emptyList() }
        itemDao.insertAll(dummyItems)
        itemRepository.fetchNewerItems()
        verify { runBlocking { mockItemService.listItems(fromId = itemDao.getMostRecentId()) } }
    }
}
