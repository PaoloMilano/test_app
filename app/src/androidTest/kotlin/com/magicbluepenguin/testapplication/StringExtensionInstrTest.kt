package com.magicbluepenguin.testapplication

import android.graphics.Bitmap
import com.magicbluepenguin.testapplication.util.toBitMap
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import testImageItem

/**
 * Surprisingly, this functionality needs the Android framework in order to work
 * correctly, which is why it's an instrumented test
 */
class StringExtensionInstrTest {

    @Test
    fun testThatImageStringCanBeParsedToBitmap() {
        assertTrue(testImageItem.img.toBitMap() is Bitmap)
    }

    @Test
    fun testThatNonImageStringCanBeNotParsedToBitmap() {
        assertNull("not a bitmap".toBitMap())
    }
}
