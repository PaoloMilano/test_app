package com.magicbluepenguin.testapplication.data.cache

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.magicbluepenguin.testapplication.data.models.Item

@Dao
interface ItemDao {

    @Query("SELECT * FROM item")
    fun getAllItems(): DataSource.Factory<Int, Item>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(users: List<Item>)
}