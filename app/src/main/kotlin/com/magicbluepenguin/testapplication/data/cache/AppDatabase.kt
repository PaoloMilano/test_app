package com.magicbluepenguin.testapplication.data.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.magicbluepenguin.testapplication.data.models.ImageItem

@Database(entities = arrayOf(ImageItem::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun imageItemDao(): ImageItemDao
}