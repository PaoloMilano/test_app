package com.magicbluepenguin.testapplication

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.magicbluepenguin.testapplication.data.cache.ItemDao
import dummyItems
import getInMemoryDb
import modifiedDummyItems
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ItemDaoInstrTest {

    private var _itemDao: ItemDao? = null
    private val itemDao: ItemDao
        get() = _itemDao!!

    @Before
    fun setUp() {
        _itemDao = getInMemoryDb(InstrumentationRegistry.getInstrumentation().context).itemDao()
        assertTrue(itemDao.isEmpty())
    }

    @Test
    fun testIsEmpty() {
        assertEquals(itemDao.isEmpty(), itemDao.getAllItems().isEmpty())
        itemDao.insertAll(dummyItems)
        assertFalse(itemDao.isEmpty())
        assertEquals(itemDao.isEmpty(), itemDao.getAllItems().isEmpty())
    }

    @Test
    fun testInsertingAll() {
        itemDao.insertAll(dummyItems)
        assertEquals(dummyItems.sortedBy { it._id }.reversed(), itemDao.getAllItems())
    }

    @Test
    fun testDoubleInsertResultsInUpdate() {
        itemDao.insertAll(dummyItems)
        itemDao.insertAll(modifiedDummyItems)
        assertEquals(modifiedDummyItems.sortedBy { it._id }.reversed(), itemDao.getAllItems())
    }

    @Test
    fun testDeletingAll() {
        itemDao.insertAll(dummyItems)

        assertFalse(itemDao.isEmpty())

        itemDao.deleteAll()
        assertTrue(itemDao.isEmpty())
    }

    @Test
    fun testDeletingWithNotIn() {
        itemDao.insertAll(dummyItems)

        // Exclude the first 2
        val excludedItems = dummyItems.take(2)
        itemDao.deleteAllExcluding(excludedItems.map { it._id })

        // Check that the first 2 were kept
        assertFalse(itemDao.getAllItems().isEmpty())
        assertEquals(excludedItems.sortedBy { it._id }.reversed(), itemDao.getAllItems())
    }
}
