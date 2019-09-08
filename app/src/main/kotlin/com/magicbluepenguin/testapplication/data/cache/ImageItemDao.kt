package com.magicbluepenguin.testapplication.data.cache

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.magicbluepenguin.testapplication.data.models.ImageItem

@Dao
interface ImageItemDao {

    @Query("SELECT * FROM ImageItem ORDER BY _id DESC")
    fun getAllItemsPaged(): DataSource.Factory<Int, ImageItem>

    @Query("SELECT * FROM ImageItem ORDER BY _id DESC")
    fun getAllItems(): List<ImageItem>

    @Query("SELECT MAX(_id) FROM ImageItem")
    fun getMostRecentId(): String?

    @Query("SELECT MIN(_id) FROM ImageItem")
    fun getOldestId(): String?

    @Query("SELECT * FROM ImageItem LIMIT :limit OFFSET :offset")
    fun getItems(offset: Int, limit: Int): List<ImageItem>

    @Query("SELECT(CASE WHEN NOT EXISTS(SELECT NULL FROM ImageItem) THEN 1 ELSE 0 END) AS isEmpty")
    fun isEmpty(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(items: List<ImageItem>)

    @Query("DELETE FROM ImageItem")
    fun deleteAll(): Int

    @Query("DELETE FROM ImageItem WHERE _id NOT IN (:itemsId)")
    fun deleteAllExcluding(itemsId: List<String>): Int
}