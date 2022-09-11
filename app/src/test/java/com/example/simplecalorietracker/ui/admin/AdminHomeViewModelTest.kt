package com.example.simplecalorietracker.ui.admin

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.simplecalorietracker.data.FakeFoodEntryRepositoryImpl
import com.example.simplecalorietracker.data.entity.FoodEntryEntity
import com.example.simplecalorietracker.domain.usecase.*
import com.example.simplecalorietracker.utils.TestSchedulerImpl
import io.reactivex.rxjava3.subscribers.TestSubscriber
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AdminHomeViewModelTest {

    private lateinit var viewModel: AdminHomeViewModel
    private lateinit var fakeRepository: FakeFoodEntryRepositoryImpl
    private lateinit var getFoodEntriesRemoteUsecase: GetFoodEntriesRemoteUsecase
    private lateinit var updateLocalFoodEntriesUsecase: UpdateLocalFoodEntriesUsecase
    private lateinit var getFoodEntriesRemoteAndUpdateCacheUsecase: GetFoodEntriesRemoteAndUpdateCacheUsecase
    private lateinit var getFoodEntriesLocalUsecase: GetFoodEntriesLocalUsecase
    private lateinit var deleteFoodEntryUsecase: DeleteFoodEntryUsecase


    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        fakeRepository = FakeFoodEntryRepositoryImpl()
        getFoodEntriesRemoteUsecase = GetFoodEntriesRemoteUsecase(fakeRepository)
        updateLocalFoodEntriesUsecase = UpdateLocalFoodEntriesUsecase(fakeRepository)
        getFoodEntriesRemoteAndUpdateCacheUsecase = GetFoodEntriesRemoteAndUpdateCacheUsecase(
            getFoodEntriesRemoteUsecase,
            updateLocalFoodEntriesUsecase
        )
        getFoodEntriesLocalUsecase = GetFoodEntriesLocalUsecase(fakeRepository)
        deleteFoodEntryUsecase = DeleteFoodEntryUsecase(fakeRepository)
        viewModel = AdminHomeViewModel(
            getFoodEntriesLocalUsecase,
            deleteFoodEntryUsecase,
            getFoodEntriesRemoteAndUpdateCacheUsecase,
            TestSchedulerImpl()
        )
    }

    @Test
    fun `Should fetch food entries and update cache`() {
        viewModel.getFoodEntries(0, 0)

        val result = getFoodEntriesLocalUsecase()
        val subscriber = TestSubscriber<List<FoodEntryEntity>>()

        result.subscribe(subscriber)
        subscriber.assertNoErrors()
        subscriber.assertComplete()

        val listResult = subscriber.values()[0]

        assertTrue(listResult.isNotEmpty())
    }

    @Test
    fun `Should delete foodEntry in remote and cache`() {
        viewModel.getFoodEntries(0, 0)

        val resultBefore = getFoodEntriesLocalUsecase()
        val subscriberBefore = TestSubscriber<List<FoodEntryEntity>>()

        resultBefore.subscribe(subscriberBefore)
        subscriberBefore.assertNoErrors()
        subscriberBefore.assertComplete()

        val listBefore = subscriberBefore.values()[0]

        viewModel.deleteFoodEntry(listBefore[0])

        val result = getFoodEntriesLocalUsecase()
        val subscriber = TestSubscriber<List<FoodEntryEntity>>()

        result.subscribe(subscriber)
        subscriber.assertNoErrors()
        subscriber.assertComplete()

        val listResult = subscriber.values()[0]

        assertTrue(listBefore.size - listResult.size == 1)
    }
}