package com.example.simplecalorietracker.domain.usecase

import com.example.simplecalorietracker.data.FakeFoodEntryRepositoryImpl
import com.example.simplecalorietracker.data.entity.FoodEntryEntity
import io.reactivex.rxjava3.observers.TestObserver
import io.reactivex.rxjava3.subscribers.TestSubscriber
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FoodEntriesUsecaseTest {

    private lateinit var fakeRepository: FakeFoodEntryRepositoryImpl
    private lateinit var getFoodEntriesRemoteUsecase: GetFoodEntriesRemoteUsecase
    private lateinit var updateLocalFoodEntriesUsecase: UpdateLocalFoodEntriesUsecase
    private lateinit var getFoodEntriesRemoteAndUpdateCacheUsecase: GetFoodEntriesRemoteAndUpdateCacheUsecase
    private lateinit var getFoodEntriesLocalUsecase: GetFoodEntriesLocalUsecase
    private lateinit var deleteFoodEntryUsecase: DeleteFoodEntryUsecase

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
    }

    @Test
    fun `Should get remote data, update local cache if no filter and return list sorted by timestamp`() {
        val result = getFoodEntriesRemoteAndUpdateCacheUsecase(0, 0)
        val observer = TestObserver<List<FoodEntryEntity>>()

        result.subscribe(observer)
        observer.assertNoErrors()
        observer.assertComplete()

        val listResult = observer.values()[0]
        assertTrue(listResult.isNotEmpty())

        for (i in 0 until listResult.size - 1) {
            assertTrue(listResult[i].timestamp < listResult[i + 1].timestamp)
        }

        val localResult = fakeRepository.getFoodEntriesLocal()
        val localResultSubscriber = TestSubscriber<List<FoodEntryEntity>>()

        localResult.subscribe(localResultSubscriber)
        localResultSubscriber.assertNoErrors()
        localResultSubscriber.assertComplete()

        //Room will return in random order
        val listLocalResult = localResultSubscriber.values()[0].sortedBy { it.timestamp }
        assertEquals(listResult, listLocalResult)
    }

    @Test
    fun `Should get remote data, NOT update local cache if filter and return list sorted by timestamp`() {
        val result = getFoodEntriesRemoteAndUpdateCacheUsecase(1, 2)
        val observer = TestObserver<List<FoodEntryEntity>>()

        result.subscribe(observer)
        observer.assertNoErrors()
        observer.assertComplete()

        val listResult = observer.values()[0]
        assertTrue(listResult.isNotEmpty())

        for (i in 0 until listResult.size - 1) {
            assertTrue(listResult[i].timestamp < listResult[i + 1].timestamp)
        }

        val localResult = fakeRepository.getFoodEntriesLocal()
        val localResultSubscriber = TestSubscriber<List<FoodEntryEntity>>()

        localResult.subscribe(localResultSubscriber)
        localResultSubscriber.assertNoErrors()
        localResultSubscriber.assertComplete()

        val listLocalResult = localResultSubscriber.values()[0]

        assertTrue(listLocalResult.isEmpty())
    }

    @Test
    fun `Should get local data sorted by timestamp`() {
        //Populate local
        getFoodEntriesRemoteAndUpdateCacheUsecase(0, 0).subscribe()

        val result = getFoodEntriesLocalUsecase()
        val subscriber = TestSubscriber<List<FoodEntryEntity>>()

        result.subscribe(subscriber)
        subscriber.assertNoErrors()
        subscriber.assertComplete()

        val listResult = subscriber.values()[0]

        for (i in 0 until listResult.size - 1) {
            assertTrue(listResult[i].timestamp < listResult[i + 1].timestamp)
        }

        assertTrue(listResult.isNotEmpty())
    }

    @Test
    fun `Should delete item in local and remote`() {
        //Populate local
        val resultBase = getFoodEntriesRemoteAndUpdateCacheUsecase(0, 0)
        val baseObserver = TestObserver<List<FoodEntryEntity>>()

        resultBase.subscribe(baseObserver)
        baseObserver.assertNoErrors()
        baseObserver.assertComplete()

        val baseList = baseObserver.values()[0]

        val result = deleteFoodEntryUsecase(baseList[0])
        val observer = TestObserver<List<FoodEntryEntity>>()

        result.subscribe(observer)
        observer.assertNoErrors()
        observer.assertComplete()

        val resultRemoteAfter = getFoodEntriesRemoteUsecase(0, 0)
        val baseRemoteAfterObserver = TestObserver<List<FoodEntryEntity>>()

        resultRemoteAfter.subscribe(baseRemoteAfterObserver)
        baseRemoteAfterObserver.assertNoErrors()
        baseRemoteAfterObserver.assertComplete()

        val resultLocal = baseRemoteAfterObserver.values()[0]

        assertTrue(baseList.size - resultLocal.size == 1)
        assertTrue(resultLocal.find { it == baseList[0] } == null)
    }


    @Test
    fun `Should update local entries with new data and remove old data`() {
        //Populate local
        val resultBase = getFoodEntriesRemoteAndUpdateCacheUsecase(0, 0)
        val baseObserver = TestObserver<List<FoodEntryEntity>>()
        resultBase.subscribe(baseObserver)

        val newList = listOf(
            FoodEntryEntity(
                id = 99,
                timestamp = 99,
                name = "testFood",
                calorie = 99
            )
        )
        val result = updateLocalFoodEntriesUsecase(newList)
        val observer = TestObserver<List<FoodEntryEntity>>()

        result.subscribe(observer)
        observer.assertNoErrors()
        observer.assertComplete()

        val resultRemoteAfter = getFoodEntriesLocalUsecase()
        val subscriber = TestSubscriber<List<FoodEntryEntity>>()

        resultRemoteAfter.subscribe(subscriber)
        subscriber.assertNoErrors()
        subscriber.assertComplete()

        val resultLocal = subscriber.values()[0]

        assertTrue(resultLocal.size == 1)
        assertTrue(resultLocal[0] == newList[0])
    }
}