package com.magicbluepenguin.testapplication.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64

fun String.toBitMap(): Bitmap? {
    try {
        val decodedString = Base64.decode(this, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    } catch (e: Exception) { }
    return null
}
