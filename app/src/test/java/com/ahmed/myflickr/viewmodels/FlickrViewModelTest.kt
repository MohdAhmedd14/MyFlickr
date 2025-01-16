package com.ahmed.myflickr.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.ahmed.myflickr.repository.FlickrRepository
import com.ahmed.myflickr.repository.Resource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class FlickrViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val repository: FlickrRepository = mockk()
    private lateinit var viewModel: FlickrViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel = FlickrViewModel(repository)
    }

    @Test
    fun `when repository returns success, uiState should update with images`() = runTest {
        val mockImages = listOf(
            FlickrImage(
                title = "Test Image",
                description = "Test Description",
                author = "Test Author",
                published = "2025-01-01",
                media = Media("https://example.com/image.jpg")
            )
        )
        coEvery { repository.searchImages(any()) } returns flowOf(Resource.Success(mockImages))

        viewModel.onQueryChange("Test")
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(false, state.isLoading)
            assertEquals(mockImages, state.images)
        }
    }

    @Test
    fun `when repository returns error, uiState should update with empty images`() = runTest {
        coEvery { repository.searchImages(any()) } returns flowOf(Resource.Error(Exception("Network error")))

        viewModel.onQueryChange("ErrorTest")
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(false, state.isLoading)
            assertEquals(emptyList<FlickrImage>(), state.images)
        }
    }

    @Test
    fun `when query is empty, uiState should remain unchanged`() = runTest {
        viewModel.onQueryChange("")
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(false, state.isLoading)
            assertEquals(emptyList<FlickrImage>(), state.images)
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
