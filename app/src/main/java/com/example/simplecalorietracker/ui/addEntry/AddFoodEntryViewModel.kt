package com.example.simplecalorietracker.ui.addEntry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplecalorietracker.data.entity.FoodEntryEntity
import com.example.simplecalorietracker.domain.usecase.AddFoodEntryToLocalUsecase
import com.example.simplecalorietracker.domain.usecase.CreateFoodEntryUsecase
import com.example.simplecalorietracker.domain.usecase.UpdateFoodEntryUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AddFoodEntryViewModel @Inject constructor(
    val createFoodEntryUsecase: CreateFoodEntryUsecase,
    val updateFoodEntryUsecase: UpdateFoodEntryUsecase,
    val addFoodEntryToLocalUsecase: AddFoodEntryToLocalUsecase
) : ViewModel() {

    private val _viewState = MutableLiveData<AddFoodEntryViewState>(AddFoodEntryViewState.Normal)
    val viewState: LiveData<AddFoodEntryViewState> = _viewState

    private val _dateTime = MutableLiveData<Long>()
    val dateTime: LiveData<Long> = _dateTime
    private val disposable = CompositeDisposable()

    fun updateDateTime(dateTime: Long) {
        _dateTime.postValue(dateTime)
    }

    fun setEditState(foodEntryEntity: FoodEntryEntity?) {
        if (foodEntryEntity == null) {
            _viewState.postValue(AddFoodEntryViewState.Normal)
        } else {
            _viewState.postValue(AddFoodEntryViewState.Edit(foodEntryEntity))
            _dateTime.postValue(foodEntryEntity.timestamp)
        }
    }

    fun submit(
        foodName: String,
        foodCalorie: Long,
        timestamp: Long,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        createFoodEntryUsecase(
            foodName = foodName,
            foodCalorie = foodCalorie,
            timestamp = timestamp
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                addFoodEntryToLocalUsecase(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { Timber.d("Food entry added to cache") }
                    .also { dis -> disposable.add(dis) }
                onSuccess()
                Timber.d("Food Entry Successfully Added!")
            }, {
                onError()
                Timber.e("ERROR!! Adding Food Entry")
            }).also { dis -> disposable.add(dis) }
    }

    fun update(
        id: Int,
        foodName: String,
        foodCalorie: Long,
        timestamp: Long,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        updateFoodEntryUsecase(
            id = id,
            foodName = foodName,
            foodCalorie = foodCalorie,
            timestamp = timestamp
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                addFoodEntryToLocalUsecase(FoodEntryEntity(id, timestamp, foodName, foodCalorie))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { Timber.d("Food entry updated in cache") }
                    .also { dis -> disposable.add(dis) }
                onSuccess()
                Timber.d("Food Entry Updated Successfully!")
            }, {
                onError()
                Timber.e("ERROR!! Updating Food Entry")
            }).also { dis -> disposable.add(dis) }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}