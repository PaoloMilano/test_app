package com.magicbluepenguin.testapplication.ui.main.itemsfragment.viewmodel.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.magicbluepenguin.testapplication.data.models.Item
import com.magicbluepenguin.testapplication.util.RepositoryState

interface ItemsRepository {

    fun connect(): LiveData<PagedList<Item>>

    fun setOnRepositoryStateListener(listener: (RepositoryState) -> Unit)

    suspend fun refresh()

    suspend fun fetchNextItems()
}