package com.example.simplecalorietracker.di

import android.app.Application
import androidx.room.Room
import com.example.simplecalorietracker.BuildConfig
import com.example.simplecalorietracker.data.FoodEntryRepositoryImpl
import com.example.simplecalorietracker.data.local.FoodEntryDao
import com.example.simplecalorietracker.data.local.FoodEntryDatabase
import com.example.simplecalorietracker.domain.repository.FoodEntryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
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
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(createClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
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
    fun provideFoodEntryRepository(foodEntryDao: FoodEntryDao): FoodEntryRepository {
        return FoodEntryRepositoryImpl(foodEntryDao)
    }
}
