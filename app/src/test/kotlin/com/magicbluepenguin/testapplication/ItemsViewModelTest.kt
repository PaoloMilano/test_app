package com.magicbluepenguin.testapplication

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.magicbluepenguin.testapplication.data.models.Item
import com.magicbluepenguin.testapplication.ui.main.itemsfragment.viewmodel.ItemsViewModel
import com.magicbluepenguin.testapplication.ui.main.itemsfragment.viewmodel.repository.ItemsRepository
import com.magicbluepenguin.testapplication.util.RepositoryState
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verifyOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class ItemsViewModelTest {

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
        ItemsViewModel(spyItemsRepository)

        runBlocking {
            launch(Dispatchers.Main) {

                val listener = spyItemsRepository.listener
                verifyOrder {
                    spyItemsRepository.setOnRepositoryStateListener(listener)
                    runBlocking { spyItemsRepository.refresh() }
                    // TODO add call for check to `connect`
                }
            }
        }
    }

    class TestItemsRepository : ItemsRepository {

        var _listener: ((RepositoryState) -> Unit)? = null
        val listener: ((RepositoryState) -> Unit)
            get() = _listener!!

        override fun connect(): LiveData<PagedList<Item>> {
            return mockk()
        }

        override fun setOnRepositoryStateListener(listener: (RepositoryState) -> Unit) {
            _listener = listener
        }

        override suspend fun refresh() {
        }

        override suspend fun fetchNextItems() {
        }
    }
}