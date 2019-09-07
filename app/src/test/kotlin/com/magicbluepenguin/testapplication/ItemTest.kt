package com.magicbluepenguin.testapplication

import com.magicbluepenguin.testapplication.data.models.Item
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Test

class ItemTest {

    @Test
    fun `test that items have the same content`() {
        val item1 = Item("a", "b", "c", 0.1f)
        val item2 = Item("a", "b", "c", 0.1f)
        assertTrue(item1.hasSameContents(item2))
    }

    @Test
    fun `test that items have the same id but not same content`() {
        val item1 = Item("a", "b", "c", 0.1f)
        val item2 = Item("a", "bb", "cc", 1.1f)
        assertFalse(item1.hasSameContents(item2))
        assertTrue(item1.hasSameId(item2))
    }
}