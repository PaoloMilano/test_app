package com.magicbluepenguin.testapplication

import com.magicbluepenguin.testapplication.data.models.ImageItem
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ImageItemTest {

    @Test
    fun `test that items have the same content`() {
        val item1 = ImageItem("a", "b", "c", 0.1f)
        val item2 = ImageItem("a", "b", "c", 0.1f)
        assertTrue(item1.hasSameContents(item2))
    }

    @Test
    fun `test that items have the same id but not same content`() {
        val item1 = ImageItem("a", "b", "c", 0.1f)
        val item2 = ImageItem("a", "bb", "cc", 1.1f)
        assertFalse(item1.hasSameContents(item2))
        assertTrue(item1.hasSameId(item2))
    }
}