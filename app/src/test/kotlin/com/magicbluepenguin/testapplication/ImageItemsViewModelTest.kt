package com.magicbluepenguin.testapplication

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.magicbluepenguin.testapplication.data.models.ImageItem
import com.magicbluepenguin.testapplication.ui.main.itemsfragment.viewmodel.ImageItemsViewModel
import com.magicbluepenguin.testapplication.ui.main.itemsfragment.viewmodel.repository.ImageItemsRepository
import com.magicbluepenguin.testapplication.util.RepositoryState
import io.mockk.mockk
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verifySequence
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class ImageItemsViewModelTest {

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    val spyItemsRepository = spyk<TestItemsRepository>()

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun `test initialisation`() {
        ImageItemsViewModel(spyItemsRepository)
        val listenerSlot = slot<(RepositoryState) -> Unit>()
        verifySequence {
            spyItemsRepository.setOnRepositoryStateListener(capture(listenerSlot))
        }
        assertEquals(listenerSlot.captured, spyItemsRepository.listener)
    }

    @Test
    fun `test datasource connection`() = runBlocking {
        var dataSource: LiveData<PagedList<ImageItem>>? = null
        ImageItemsViewModel(spyItemsRepository)
            .connectToDataStream { dataSource = it }.join()
        assertEquals(spyItemsRepository.mockDataSource, dataSource)
    }

    class TestItemsRepository : ImageItemsRepository {

        val mockDataSource = mockk<LiveData<PagedList<ImageItem>>>()

        override suspend fun fetchNewerItems() {
        }

        var _listener: ((RepositoryState) -> Unit)? = null
        val listener: ((RepositoryState) -> Unit)
            get() = _listener!!

        override fun connect(): LiveData<PagedList<ImageItem>> {
            return mockDataSource
        }

        override fun setOnRepositoryStateListener(listener: (RepositoryState) -> Unit) {
            _listener = listener
        }

        override suspend fun refresh() {
        }

        override suspend fun fetchOlderItems() {
        }
    }
}