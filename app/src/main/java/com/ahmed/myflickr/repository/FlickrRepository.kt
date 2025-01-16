package com.ahmed.myflickr.repository

import com.ahmed.myflickr.api.FlickrApi
import com.ahmed.myflickr.viewmodels.FlickrImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val exception: Throwable) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
}

interface FlickrRepository {
    fun searchImages(query: String): Flow<Resource<List<FlickrImage>>>
}

class FlickrRepositoryImpl @Inject constructor(private val api: FlickrApi) : FlickrRepository {
    override fun searchImages(query: String): Flow<Resource<List<FlickrImage>>> = flow {
        try {
            val response = api.searchImages(query)
            emit(Resource.Success(response.items))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }.flowOn(Dispatchers.IO)
}