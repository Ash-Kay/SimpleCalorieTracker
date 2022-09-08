package com.example.simplecalorietracker.di

import android.app.Application
import androidx.room.Room
import com.example.simplecalorietracker.BuildConfig
import com.example.simplecalorietracker.data.FoodEntryRepositoryImpl
import com.example.simplecalorietracker.data.local.FoodEntryDao
import com.example.simplecalorietracker.data.local.FoodEntryDatabase
import com.example.simplecalorietracker.data.remote.RetrofitService
import com.example.simplecalorietracker.domain.repository.FoodEntryRepository
import com.example.simplecalorietracker.utils.NetworkHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(app: Application): FoodEntryDatabase {
        return Room.databaseBuilder(
            app,
            FoodEntryDatabase::class.java,
            FoodEntryDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideFoodEntryDao(db: FoodEntryDatabase): FoodEntryDao {
        return db.foodEntryDao
    }

    @Provides
    @Singleton
    fun provideRetrofitService(): RetrofitService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(createClient())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitService::class.java)
    }

    private fun createClient(): OkHttpClient {
        val okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
            okHttpClientBuilder.addInterceptor(loggingInterceptor)
        }
        return okHttpClientBuilder.build()
    }

    @Provides
    @Singleton
    fun provideFoodEntryRepository(
        foodEntryDao: FoodEntryDao,
        retrofitService: RetrofitService,
        networkHandler: NetworkHandler
    ): FoodEntryRepository {
        return FoodEntryRepositoryImpl(foodEntryDao, retrofitService, networkHandler)
    }
}
