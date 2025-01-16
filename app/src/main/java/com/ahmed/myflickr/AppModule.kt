package com.ahmed.myflickr

import com.ahmed.myflickr.api.FlickrApi
import com.ahmed.myflickr.repository.FlickrRepository
import com.ahmed.myflickr.repository.FlickrRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.flickr.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideFlickrApi(retrofit: Retrofit): FlickrApi = retrofit.create(FlickrApi::class.java)

    @Provides
    @Singleton
    fun provideFlickrRepository(api: FlickrApi): FlickrRepository = FlickrRepositoryImpl(api)
}
