package com.alltrails.restaurantdiscovery.di

import com.alltrails.restaurantdiscovery.api.GooglePlacesApiService
import com.alltrails.restaurantdiscovery.data.DefaultRestaurantsRepository
import com.alltrails.restaurantdiscovery.data.RestaurantsRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton



@Module
@InstallIn(SingletonComponent::class)
abstract class GooglePlacesModule {
    companion object {
        private const val BASE_URL = "https://maps.googleapis.com/"

        @Provides
        @Singleton
        fun provideMoshi(): Moshi {
            return Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        }

        @Provides
        @Singleton

        fun provideRetrofit(moshi: Moshi): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
        }

        @Provides
        @Singleton
        fun providePlacesApiService(retrofit: Retrofit): GooglePlacesApiService {
            return retrofit.create(GooglePlacesApiService::class.java)
        }
    }
    @Binds
    abstract fun bindStocksRepository(repository: DefaultRestaurantsRepository): RestaurantsRepository
}