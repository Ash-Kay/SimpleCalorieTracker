package com.example.simplecalorietracker.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.simplecalorietracker.BuildConfig
import com.example.simplecalorietracker.data.AuthRepositoryImpl
import com.example.simplecalorietracker.data.FoodEntryRepositoryImpl
import com.example.simplecalorietracker.data.local.FoodEntryDao
import com.example.simplecalorietracker.data.local.FoodEntryDatabase
import com.example.simplecalorietracker.data.remote.RetrofitService
import com.example.simplecalorietracker.domain.repository.AuthRepository
import com.example.simplecalorietracker.domain.repository.FoodEntryRepository
import com.example.simplecalorietracker.utils.AuthUtils
import com.example.simplecalorietracker.utils.NetworkHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun provideRetrofitService(okHttpClient: OkHttpClient): RetrofitService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitService::class.java)
    }

    @Provides
    @Singleton
    fun createClient(@ApplicationContext context: Context): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG)
            logging.level = HttpLoggingInterceptor.Level.BODY
        else
            logging.level = HttpLoggingInterceptor.Level.NONE

        val chuckerInterceptor = ChuckerInterceptor.Builder(context)
            .collector(ChuckerCollector(context))
            .maxContentLength(250000L)
            .redactHeaders(emptySet())
            .alwaysReadResponseBody(false)
            .build()

        return OkHttpClient.Builder()
            .addInterceptor(chuckerInterceptor)
            .addInterceptor(logging)
            .addInterceptor {
                it.proceed(
                    it.request().newBuilder()
                        .addHeader("Authorization", AuthUtils.getAuthToken(context).orEmpty())
                        .build()
                )
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideFoodEntryRepository(
        foodEntryDao: FoodEntryDao,
        retrofitService: RetrofitService
    ): FoodEntryRepository {
        return FoodEntryRepositoryImpl(foodEntryDao, retrofitService)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        retrofitService: RetrofitService
    ): AuthRepository {
        return AuthRepositoryImpl(retrofitService)
    }
}
