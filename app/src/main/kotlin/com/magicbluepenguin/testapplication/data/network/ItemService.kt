package com.magicbluepenguin.testapplication.data.network

import com.magicbluepenguin.testapplication.data.models.Item
import retrofit2.http.GET
import retrofit2.http.Query

interface ItemService {

    @GET("items")
    suspend fun listItems(@Query("max_id") lowestId: String? = null): List<Item>
}