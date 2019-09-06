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
    fun getAllItemsPaged(): DataSource.Factory<Int, Item>

    @Query("SELECT * FROM item")
    fun getAllItems(): List<Item>

    @Query("SELECT * FROM item LIMIT :limit OFFSET :offset")
    fun getItems(offset: Int, limit: Int): List<Item>

    @Query("SELECT(CASE WHEN NOT EXISTS(SELECT NULL FROM item) THEN 1 ELSE 0 END) AS isEmpty")
    fun isEmpty(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(items: List<Item>)

    @Query("DELETE FROM item")
    fun deleteAll(): Int

    @Query("DELETE FROM item WHERE img NOT IN (:itemsWithImg)")
    fun deleteAllExcluding(itemsWithImg: List<String>): Int
}