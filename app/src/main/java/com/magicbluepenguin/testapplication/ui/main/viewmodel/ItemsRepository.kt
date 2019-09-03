package com.magicbluepenguin.testapplication.ui.main.viewmodel

import com.magicbluepenguin.testapplication.data.cache.AppDatabase
import com.magicbluepenguin.testapplication.data.models.Item
import com.magicbluepenguin.testapplication.data.network.RetrofitServiceProvider

class ItemsRepository(
    val retrofitServiceProvider: RetrofitServiceProvider,
    val appDatabase: AppDatabase
) {

    suspend fun fetchNextItems(): ItemsFetchResponse {
        appDatabase.itemDao().insertAll(retrofitServiceProvider.itemService.listItems())
        return ItemsFetchResponse(appDatabase.itemDao().getAllItems(), false, false)
    }

    data class ItemsFetchResponse(val items: List<Item>, val hasMore: Boolean, val error: Boolean)
}