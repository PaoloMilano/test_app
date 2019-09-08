package com.magicbluepenguin.testapplication

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.magicbluepenguin.testapplication.data.cache.ImageItemDao
import com.magicbluepenguin.testapplication.data.models.ImageItem
import com.magicbluepenguin.testapplication.data.network.ItemService
import com.magicbluepenguin.testapplication.ui.main.itemsfragment.viewmodel.repository.CachedImageItemsRepository
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
    private var _imageItemRepository: CachedImageItemsRepository? = null
    private val itemRepository: CachedImageItemsRepository
        get() = _imageItemRepository!!

    private var _imageItemDao: ImageItemDao? = null
    private val imageItemDao: ImageItemDao
        get() = _imageItemDao!!

    @Before
    fun setUp() {
        _imageItemDao = getInMemoryDb(InstrumentationRegistry.getInstrumentation().context).imageItemDao()
        _imageItemRepository =
            CachedImageItemsRepository(
                mockItemService,
                imageItemDao
            )
    }

    @Test
    fun testConnection() {
        imageItemDao.insertAll(dummyItems)
        val latch = CountDownLatch(1)
        val itemsCatcher = mutableListOf<ImageItem>()
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
        val itemsCatcher = mutableListOf<ImageItem>()
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            itemRepository.connect().observeForever {
                it?.let {
                    itemsCatcher.addAll(it)
                }
                latch.countDown()
            }
        }
        imageItemDao.insertAll(dummyItems)

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
        imageItemDao.insertAll(dummyItems)
        itemRepository.fetchOlderItems()
        verify { runBlocking { mockItemService.listItems(untilId = imageItemDao.getOldestId()) } }
    }

    @Test
    fun testFetchingNewerBatch() = runBlocking {
        every { runBlocking { mockItemService.listItems(any()) } } answers { emptyList() }
        imageItemDao.insertAll(dummyItems)
        itemRepository.fetchNewerItems()
        verify { runBlocking { mockItemService.listItems(fromId = imageItemDao.getMostRecentId()) } }
    }
}
