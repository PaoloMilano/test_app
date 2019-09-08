package com.magicbluepenguin.testapplication

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.magicbluepenguin.testapplication.data.cache.ImageItemDao
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
class ImageItemDaoInstrTest {

    private var _imageItemDao: ImageItemDao? = null
    private val imageItemDao: ImageItemDao
        get() = _imageItemDao!!

    @Before
    fun setUp() {
        _imageItemDao = getInMemoryDb(InstrumentationRegistry.getInstrumentation().context).imageItemDao()
        assertTrue(imageItemDao.isEmpty())
    }

    @Test
    fun testIsEmpty() {
        assertEquals(imageItemDao.isEmpty(), imageItemDao.getAllItems().isEmpty())
        imageItemDao.insertAll(dummyItems)
        assertFalse(imageItemDao.isEmpty())
        assertEquals(imageItemDao.isEmpty(), imageItemDao.getAllItems().isEmpty())
    }

    @Test
    fun testInsertingAll() {
        imageItemDao.insertAll(dummyItems)
        assertEquals(dummyItems.sortedBy { it._id }.reversed(), imageItemDao.getAllItems())
    }

    @Test
    fun testDoubleInsertResultsInUpdate() {
        imageItemDao.insertAll(dummyItems)
        imageItemDao.insertAll(modifiedDummyItems)
        assertEquals(modifiedDummyItems.sortedBy { it._id }.reversed(), imageItemDao.getAllItems())
    }

    @Test
    fun testDeletingAll() {
        imageItemDao.insertAll(dummyItems)

        assertFalse(imageItemDao.isEmpty())

        imageItemDao.deleteAll()
        assertTrue(imageItemDao.isEmpty())
    }

    @Test
    fun testDeletingWithNotIn() {
        imageItemDao.insertAll(dummyItems)

        // Exclude the first 2
        val excludedItems = dummyItems.take(2)
        imageItemDao.deleteAllExcluding(excludedItems.map { it._id })

        // Check that the first 2 were kept
        assertFalse(imageItemDao.getAllItems().isEmpty())
        assertEquals(excludedItems.sortedBy { it._id }.reversed(), imageItemDao.getAllItems())
    }
}
