package com.ahmed.myflickr.api

import com.ahmed.myflickr.viewmodels.FlickrImage
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrApi {
    @GET("services/feeds/photos_public.gne?format=json&nojsoncallback=1")
    suspend fun searchImages(@Query("tags") tags: String): FlickrResponse
}

data class FlickrResponse(val items: List<FlickrImage>)