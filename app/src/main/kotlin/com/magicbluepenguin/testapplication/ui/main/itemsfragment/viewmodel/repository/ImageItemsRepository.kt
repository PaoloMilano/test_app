package com.magicbluepenguin.testapplication.ui.main.itemsfragment.viewmodel.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.magicbluepenguin.testapplication.data.models.ImageItem
import com.magicbluepenguin.testapplication.util.RepositoryState

interface ImageItemsRepository {

    fun connect(): LiveData<PagedList<ImageItem>>

    fun setOnRepositoryStateListener(listener: (RepositoryState) -> Unit)

    suspend fun refresh()

    suspend fun fetchOlderItems()

    suspend fun fetchNewerItems()
}