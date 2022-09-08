package com.example.simplecalorietracker.ui.addEntry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplecalorietracker.data.entity.FoodEntryEntity
import com.example.simplecalorietracker.domain.usecase.AddFoodEntryUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class AddFoodEntryViewModel @Inject constructor(val addFoodEntryUsecase: AddFoodEntryUsecase) :
    ViewModel() {

    private val _dateTime = MutableLiveData<Long>()
    val dateTime: LiveData<Long> = _dateTime

    fun updateDateTime(dateTime: Long) {
        _dateTime.postValue(dateTime)
    }

    fun submit(foodName: String, foodCalorie: Long, timestamp: Long) {
        addFoodEntryUsecase(FoodEntryEntity(0, timestamp, foodName, foodCalorie))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                println("ASHTEST: ADDED SUCESS!!")
            }, {
                println("ASHTEST: ERR!!" + it)
            })
    }
}