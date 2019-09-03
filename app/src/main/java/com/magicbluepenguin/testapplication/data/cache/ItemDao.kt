package com.magicbluepenguin.testapplication.data.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.magicbluepenguin.testapplication.data.models.Item

@Dao
interface ItemDao {

    @Query("SELECT * FROM item")
    fun getAllItems(): List<Item>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(users: List<Item>)
}