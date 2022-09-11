package com.example.simplecalorietracker.ui.addEntry

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

class AddFoodEntryViewModelTest {

    private lateinit var viewModel: AddFoodEntryViewModel
    private lateinit var fakeRepository: FakeFoodEntryRepositoryImpl
    private lateinit var createFoodEntryUsecase: CreateFoodEntryUsecase
    private lateinit var updateFoodEntryUsecase: UpdateFoodEntryUsecase
    private lateinit var addFoodEntryToLocalUsecase: AddFoodEntryToLocalUsecase
    private lateinit var getFoodEntriesLocalUsecase: GetFoodEntriesLocalUsecase
    private lateinit var getFoodEntriesRemoteUsecase: GetFoodEntriesRemoteUsecase
    private lateinit var updateLocalFoodEntriesUsecase: UpdateLocalFoodEntriesUsecase
    private lateinit var getFoodEntriesRemoteAndUpdateCacheUsecase: GetFoodEntriesRemoteAndUpdateCacheUsecase

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        fakeRepository = FakeFoodEntryRepositoryImpl()
        createFoodEntryUsecase = CreateFoodEntryUsecase(fakeRepository)
        updateFoodEntryUsecase = UpdateFoodEntryUsecase(fakeRepository)
        addFoodEntryToLocalUsecase = AddFoodEntryToLocalUsecase(fakeRepository)
        getFoodEntriesLocalUsecase = GetFoodEntriesLocalUsecase(fakeRepository)
        getFoodEntriesRemoteUsecase = GetFoodEntriesRemoteUsecase(fakeRepository)
        updateLocalFoodEntriesUsecase = UpdateLocalFoodEntriesUsecase(fakeRepository)
        getFoodEntriesRemoteAndUpdateCacheUsecase = GetFoodEntriesRemoteAndUpdateCacheUsecase(
            getFoodEntriesRemoteUsecase,
            updateLocalFoodEntriesUsecase
        )
        viewModel = AddFoodEntryViewModel(
            createFoodEntryUsecase,
            updateFoodEntryUsecase,
            addFoodEntryToLocalUsecase,
            TestSchedulerImpl()
        )
    }

    @Test
    fun `Should update remote and local on submit`() {
        val resultBefore = getFoodEntriesLocalUsecase()
        val subscriberBefore = TestSubscriber<List<FoodEntryEntity>>()

        resultBefore.subscribe(subscriberBefore)
        subscriberBefore.assertNoErrors()
        subscriberBefore.assertComplete()

        val listBefore = subscriberBefore.values()[0]

        viewModel.submit("test", 9999, 0, {}, {})

        val result = getFoodEntriesLocalUsecase()
        val subscriber = TestSubscriber<List<FoodEntryEntity>>()

        result.subscribe(subscriber)
        subscriber.assertNoErrors()
        subscriber.assertComplete()

        val listResult = subscriber.values()[0]

        assertTrue(listResult.find { it.name == "test" } != null)
        assertTrue(listResult.size - listBefore.size == 1)
    }

    @Test
    fun `Should update remote and local on update`() {
        getFoodEntriesRemoteAndUpdateCacheUsecase(0, 0).subscribe()
        val resultBefore = getFoodEntriesLocalUsecase()
        val subscriberBefore = TestSubscriber<List<FoodEntryEntity>>()

        resultBefore.subscribe(subscriberBefore)
        subscriberBefore.assertNoErrors()
        subscriberBefore.assertComplete()

        val listBefore = subscriberBefore.values()[0]

        viewModel.update(1, "test", 9999, 0, {}, {})

        val result = getFoodEntriesLocalUsecase()
        val subscriber = TestSubscriber<List<FoodEntryEntity>>()

        result.subscribe(subscriber)
        subscriber.assertNoErrors()
        subscriber.assertComplete()

        val listResult = subscriber.values()[0]

        assertTrue(listResult.find { it.name == "test" } != null)
        assertTrue(listResult.size == listBefore.size)
    }
}