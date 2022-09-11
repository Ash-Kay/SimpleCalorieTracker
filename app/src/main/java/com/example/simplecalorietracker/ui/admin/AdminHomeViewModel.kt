package com.example.simplecalorietracker.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplecalorietracker.data.entity.FoodEntryEntity
import com.example.simplecalorietracker.domain.usecase.DeleteFoodEntryUsecase
import com.example.simplecalorietracker.domain.usecase.GetFoodEntriesLocalUsecase
import com.example.simplecalorietracker.domain.usecase.GetFoodEntriesRemoteAndUpdateCacheUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AdminHomeViewModel @Inject constructor(
    val getFoodEntriesLocalUsecase: GetFoodEntriesLocalUsecase,
    val deleteFoodEntryUsecase: DeleteFoodEntryUsecase,
    val getFoodEntriesRemoteAndUpdateCacheUsecase: GetFoodEntriesRemoteAndUpdateCacheUsecase
) : ViewModel() {

    private val _viewState = MutableLiveData<AdminHomeViewState>(AdminHomeViewState.Idle)
    val viewState: LiveData<AdminHomeViewState> = _viewState

    private val disposable = CompositeDisposable()
    private val flowDisposable = CompositeDisposable()

    init {
        //Observe local cache, if it updates, update the ui
        getCacheFoodEntries()
    }

    fun getFoodEntries(start: Long = 0, end: Long = 0) {
        _viewState.postValue(AdminHomeViewState.Loading)
        getFoodEntriesRemoteAndUpdateCacheUsecase(start, end)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _viewState.postValue(AdminHomeViewState.DataFetchSuccess(it))
                Timber.d("Remote food entry list fetch successful", it)
            }, {
                _viewState.postValue(AdminHomeViewState.Error("Error fetching data"))
                Timber.e("ERROR!! Fetching Food Entry List", it)
            }).also { dis -> disposable.add(dis) }
    }

    fun getCacheFoodEntries() {
        flowDisposable.clear()

        _viewState.postValue(AdminHomeViewState.Loading)
        getFoodEntriesLocalUsecase()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _viewState.postValue(AdminHomeViewState.CacheDataFetchSuccess(it))
                Timber.d("Local food entry list fetch successful", it)
            }, {
                _viewState.postValue(AdminHomeViewState.Error("Error fetching data"))
                Timber.e("ERROR!! Fetching Food Entry List", it)
            }).also { dis -> flowDisposable.add(dis) }
    }

    fun showNoInternetError() {
        _viewState.postValue(AdminHomeViewState.Error("No Internet!"))
    }

    fun deleteFoodEntry(foodEntry: FoodEntryEntity) {
        deleteFoodEntryUsecase(foodEntry)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Timber.d("Deletion in remote and local success!")
            }, {
                _viewState.postValue(AdminHomeViewState.Error("Error deleting data"))
                Timber.e("ERROR!! Deleting food item")
            }).also { dis -> flowDisposable.add(dis) }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}