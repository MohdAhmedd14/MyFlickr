package com.ahmed.myflickr.repository

import com.ahmed.myflickr.api.FlickrApi
import com.ahmed.myflickr.api.FlickrResponse
import com.ahmed.myflickr.viewmodels.FlickrImage
import com.ahmed.myflickr.viewmodels.Media
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class FlickrRepositoryTest {

    private val api: FlickrApi = mockk()
    private lateinit var repository: FlickrRepository

    @Before
    fun setUp() {
        repository = FlickrRepositoryImpl(api)
    }

    @Test
    fun `when API returns success, repository emits success`() = runBlocking {
        val mockResponse = FlickrResponse(
            items = listOf(
                FlickrImage(
                    title = "Test Image",
                    description = "Test Description",
                    author = "Test Author",
                    published = "2025-01-01",
                    media = Media("https://example.com/image.jpg")
                )
            )
        )
        coEvery { api.searchImages(any()) } returns mockResponse

        val results = repository.searchImages("Test").toList()

        assertEquals(1, results.size) // Loading and Success
        assert(results[0] is Resource.Success)
        assertEquals(mockResponse.items, (results[0] as Resource.Success).data)
    }

    @Test
    fun `when API throws exception, repository emits error`() = runBlocking {
        coEvery { api.searchImages(any()) } throws Exception("Network error")

        val results = repository.searchImages("ErrorTest").toList()

        assertEquals(1, results.size) // Loading and Error
        assert(results[0] is Resource.Error)
    }
}
