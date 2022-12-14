package com.example.simplecalorietracker.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplecalorietracker.data.entity.FoodEntryEntity
import com.example.simplecalorietracker.domain.usecase.DeleteFoodEntryUsecase
import com.example.simplecalorietracker.domain.usecase.GetFoodEntriesLocalUsecase
import com.example.simplecalorietracker.domain.usecase.GetFoodEntriesRemoteAndUpdateCacheUsecase
import com.example.simplecalorietracker.utils.Constants
import com.example.simplecalorietracker.utils.IScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class UserHomeViewModel @Inject constructor(
    val getFoodEntriesLocalUsecase: GetFoodEntriesLocalUsecase,
    val deleteFoodEntryUsecase: DeleteFoodEntryUsecase,
    val getFoodEntriesRemoteAndUpdateCacheUsecase: GetFoodEntriesRemoteAndUpdateCacheUsecase,
    private val scheduler: IScheduler
) : ViewModel() {

    private val _viewState = MutableLiveData<UserHomeViewState>(UserHomeViewState.Idle)
    val viewState: LiveData<UserHomeViewState> = _viewState

    private val _todayConsumption = MutableLiveData<Long>()
    val todayConsumption: LiveData<Long> = _todayConsumption

    private val disposable = CompositeDisposable()
    private val flowDisposable = CompositeDisposable()

    init {
        //Observe local cache, if it updates, update the ui
        getCacheFoodEntries()
    }

    fun getFoodEntries(start: Long = 0, end: Long = 0) {
        _viewState.postValue(UserHomeViewState.Loading)
        getFoodEntriesRemoteAndUpdateCacheUsecase(start, end)
            .subscribeOn(scheduler.io)
            .observeOn(scheduler.mainThread)
            .subscribe({
                _viewState.postValue(UserHomeViewState.DataFetchSuccess(it))
                Timber.d("Remote food entry list fetch successful", it)
            }, {
                _viewState.postValue(UserHomeViewState.Error("Error fetching data"))
                Timber.e("ERROR!! Fetching Food Entry List", it)
            }).also { dis -> disposable.add(dis) }
    }

    fun getCacheFoodEntries() {
        flowDisposable.clear()

        _viewState.postValue(UserHomeViewState.Loading)
        getFoodEntriesLocalUsecase()
            .subscribeOn(scheduler.io)
            .observeOn(scheduler.mainThread)
            .subscribe({
                updateTodayConsumption(it)
                _viewState.postValue(UserHomeViewState.CacheDataFetchSuccess(it))
                Timber.d("Local food entry list fetch successful", it)
            }, {
                _viewState.postValue(UserHomeViewState.Error("Error fetching data"))
                Timber.e("ERROR!! Fetching Food Entry List", it)
            }).also { dis -> flowDisposable.add(dis) }
    }

    fun showNoInternetError() {
        _viewState.postValue(UserHomeViewState.Error("No Internet!"))
    }

    fun deleteFoodEntry(foodEntry: FoodEntryEntity) {
        deleteFoodEntryUsecase(foodEntry)
            .subscribeOn(scheduler.io)
            .observeOn(scheduler.mainThread)
            .subscribe({
                Timber.d("Deletion in remote and local success!")
            }, {
                _viewState.postValue(UserHomeViewState.Error("Error deleting data"))
                Timber.e("ERROR!! Deleting food item")
            }).also { dis -> flowDisposable.add(dis) }
    }

    private fun updateTodayConsumption(foodEntries: List<FoodEntryEntity>) {
        val currentTimestamp = System.currentTimeMillis()

        val calStart = Calendar.getInstance()
        calStart.timeInMillis = currentTimestamp
        calStart.timeZone = TimeZone.getTimeZone("GMT")
        calStart.set(Calendar.HOUR, 0)
        calStart.set(Calendar.MINUTE, 0)
        calStart.set(Calendar.SECOND, 0)
        calStart.set(Calendar.MILLISECOND, 0)
        calStart.set(Calendar.AM_PM, 0)

        val todayStart = calStart.timeInMillis
        val todayEnd = todayStart + Constants.oneDayInMillis - 1

        val sum = foodEntries.filter { it.timestamp in todayStart..todayEnd }
            .sumOf { it.calorie }

        _todayConsumption.postValue(sum)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
        flowDisposable.clear()
    }
}