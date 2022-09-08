package com.example.simplecalorietracker.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplecalorietracker.data.entity.FoodEntryEntity
import com.example.simplecalorietracker.domain.usecase.GetFoodEntriesUsecase
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
    val getFoodEntriesUsecase: GetFoodEntriesUsecase,
    val updateLocalFoodEntriesUsecase: UpdateLocalFoodEntriesUsecase,
    val networkHandler: NetworkHandler
) : ViewModel() {

    private val _foodEntries = MutableLiveData<List<FoodEntryEntity>>()
    val foodEntries: LiveData<List<FoodEntryEntity>> = _foodEntries
    private val disposable = CompositeDisposable()

    fun getFoodEntries() {
        getFoodEntriesUsecase()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Timber.d("Food Entry List fetched successfully", it)
                _foodEntries.postValue(it)
            }, {
                Timber.e("ERROR!! Fetching Food Entry List", it)
            }).also { dis -> disposable.add(dis) }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}