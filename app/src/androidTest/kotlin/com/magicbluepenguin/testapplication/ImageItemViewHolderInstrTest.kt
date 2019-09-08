package com.magicbluepenguin.testapplication

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import com.magicbluepenguin.testapplication.data.models.ImageItem
import com.magicbluepenguin.testapplication.databinding.ListItemBinding
import com.magicbluepenguin.testapplication.ui.main.itemsfragment.Adapter.ImageItemsRecyclerViewAdapter
import com.magicbluepenguin.testapplication.util.toBitMap
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verifySequence
import kotlinx.android.synthetic.main.list_item.view.imageView
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import testImageItem
import java.io.ByteArrayOutputStream

class ImageItemViewHolderInstrTest {

    val mockImageView = mockk<ImageView>(relaxed = true)
    val mockParentView = mockk<View>(relaxed = true) {
        every { imageView } answers { mockImageView }
    }
    val mockListItemBinding = mockk<ListItemBinding>(relaxed = true) {
        every { root } answers { mockParentView }
    }
    var _imageItemViewHolder: ImageItemsRecyclerViewAdapter.ImageItemViewHolder? = null
    val imageItemViewHolder
        get() = _imageItemViewHolder!!

    @Before
    fun setUp() {
        _imageItemViewHolder =
            ImageItemsRecyclerViewAdapter.ImageItemViewHolder(mockListItemBinding)
    }

    @Test
    fun testSettingNoImageForItem() = runBlocking {

        imageItemViewHolder.bind(ImageItem("", "", "", 0.0f))

        verifySequence {
            mockImageView.setImageResource(android.R.drawable.screen_background_dark_transparent)
        }
    }

    @Test
    fun testSettingImageForItem() = runBlocking {
        imageItemViewHolder.bind(testImageItem).join()

        val bitmapSlot = slot<Bitmap>()
        verifySequence {
            mockImageView.setImageResource(android.R.drawable.screen_background_dark_transparent)
            mockImageView.setImageBitmap(capture(bitmapSlot))
        }
        compareBitmaps(testImageItem.img.toBitMap()!!, bitmapSlot.captured)
    }

    @Test
    fun testSettingImageForSameItemMultipleTimes() = runBlocking {

        imageItemViewHolder.bind(testImageItem).join()
        imageItemViewHolder.bind(testImageItem).join()
        imageItemViewHolder.bind(testImageItem).join()

        val bitmapSlot = slot<Bitmap>()
        verifySequence {
            mockImageView.setImageResource(android.R.drawable.screen_background_dark_transparent)
            mockImageView.setImageBitmap(capture(bitmapSlot))
        }
        compareBitmaps(testImageItem.img.toBitMap()!!, bitmapSlot.captured)
    }

    @Test
    fun testThatImageStringCanBeParsedToBitmap() {
        assertTrue(testImageItem.img.toBitMap() is Bitmap)
    }

    @Test
    fun testThatNonImageStringCanBeNotParsedToBitmap() {
        assertNull("not a bitmap".toBitMap())
    }

    // Using this because somehow we can't just compare bitmaps for equality
    private fun compareBitmaps(ref: Bitmap, act: Bitmap) {
        val refBytes = bitmapToByteArray(ref)
        val actBytes = bitmapToByteArray(act)

        for (i in refBytes.indices) {
            assertEquals("Compared bitmaps are not the same", refBytes.get(i), actBytes.get(i))
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap) = ByteArrayOutputStream().apply {
        bitmap.compress(
            Bitmap.CompressFormat.PNG,
            90,
            this
        )
    }.toByteArray()
}
