package com.magicbluepenguin.testapplication.data.network

import com.magicbluepenguin.testapplication.data.models.Item
import retrofit2.http.GET

interface ItemService {

    @GET("items")
    suspend fun listItems(): List<Item>
}