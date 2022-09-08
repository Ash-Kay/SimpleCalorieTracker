package com.example.simplecalorietracker.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplecalorietracker.data.entity.FoodEntryEntity
import com.example.simplecalorietracker.domain.usecase.GetFoodEntriesLocalUsecase
import com.example.simplecalorietracker.domain.usecase.GetFoodEntriesRemoteUsecase
import com.example.simplecalorietracker.domain.usecase.UpdateLocalFoodEntriesUsecase
import com.example.simplecalorietracker.utils.NetworkHandler
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
    private val networkHandler: NetworkHandler
) : ViewModel() {

    private val _foodEntries = MutableLiveData<List<FoodEntryEntity>>()
    val foodEntries: LiveData<List<FoodEntryEntity>> = _foodEntries
    private val disposable = CompositeDisposable()

    init {
        //Observe local cache, if it updates, update the ui
        getFoodEntriesLocalUsecase()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _foodEntries.postValue(it)
                Timber.d("Local food entry list fetch successful", it)
            }, {
                Timber.e("ERROR!! Fetching Food Entry List", it)
            }).also { dis -> disposable.add(dis) }
    }

    //TODO: Show toast if netowrk not avaliable
    fun getFoodEntries() {
        if (networkHandler.isNetworkAvailable()) {
            getFoodEntriesRemoteUsecase()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    updateLocalFoodEntriesUsecase(it)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { Timber.d("Local Food Entry List updated successfully", it) }
                        .also { dis -> disposable.add(dis) }
                    _foodEntries.postValue(it)
                    Timber.d("Remote food entry list fetch successful", it)
                }, {
                    Timber.e("ERROR!! Fetching Food Entry List", it)
                }).also { dis -> disposable.add(dis) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}