package com.magicbluepenguin.testapplication.data.network

import com.magicbluepenguin.testapplication.data.models.Item
import retrofit2.http.GET
import retrofit2.http.Query

interface ItemService {

    @GET("items")
    suspend fun listItems(@Query("max_id") untilId: String? = null, @Query("since_id") fromId: String? = null): List<Item>
}