package com.example.simplecalorietracker.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplecalorietracker.data.entity.FoodEntryEntity
import com.example.simplecalorietracker.domain.usecase.DeleteFoodEntryUsecase
import com.example.simplecalorietracker.domain.usecase.GetFoodEntriesLocalUsecase
import com.example.simplecalorietracker.domain.usecase.GetFoodEntriesRemoteUsecase
import com.example.simplecalorietracker.domain.usecase.UpdateLocalFoodEntriesUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UserHomeViewModel @Inject constructor(
    val getFoodEntriesRemoteUsecase: GetFoodEntriesRemoteUsecase,
    val getFoodEntriesLocalUsecase: GetFoodEntriesLocalUsecase,
    val updateLocalFoodEntriesUsecase: UpdateLocalFoodEntriesUsecase,
    val deleteFoodEntryUsecase: DeleteFoodEntryUsecase
) : ViewModel() {

    private val _viewState = MutableLiveData<UserHomeViewState>(UserHomeViewState.Idle)
    val viewState: LiveData<UserHomeViewState> = _viewState

    private val disposable = CompositeDisposable()
    private val flowDisposable = CompositeDisposable()

    init {
        //Observe local cache, if it updates, update the ui
        _viewState.postValue(UserHomeViewState.Loading)
        getFoodEntriesLocalUsecase()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _viewState.postValue(UserHomeViewState.DataFetchSuccess(it))
                Timber.d("Local food entry list fetch successful", it)
            }, {
                _viewState.postValue(UserHomeViewState.Error("Error fetching data"))
                Timber.e("ERROR!! Fetching Food Entry List", it)
            }).also { dis -> flowDisposable.add(dis) }
    }

    fun getFoodEntries(start: Long = 0, end: Long = 0) {
        _viewState.postValue(UserHomeViewState.Loading)
        getFoodEntriesRemoteUsecase(start, end)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                //Only cache if no filter applied
                if (start == 0L && end == 0L) {
                    updateLocalFoodEntriesUsecase(it)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            Timber.d("Local Food Entry List updated successfully", it)
                        }
                        .also { dis -> disposable.add(dis) }
                }
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
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
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
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Timber.d("Deletion in remote and local success!")
            }, {
                _viewState.postValue(UserHomeViewState.Error("Error deleting data"))
                Timber.e("ERROR!! Deleting food item")
            }).also { dis -> flowDisposable.add(dis) }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
        flowDisposable.clear()
    }
}