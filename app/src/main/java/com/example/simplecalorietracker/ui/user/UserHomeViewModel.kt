package com.example.simplecalorietracker.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplecalorietracker.data.entity.FoodEntryEntity
import com.example.simplecalorietracker.domain.usecase.GetFoodEntriesUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class UserHomeViewModel @Inject constructor(val getFoodEntriesUsecase: GetFoodEntriesUsecase) :
    ViewModel() {

    private val _foodEntries = MutableLiveData<List<FoodEntryEntity>>()
    val foodEntries: LiveData<List<FoodEntryEntity>> = _foodEntries

    fun getFoodEntries() {
        getFoodEntriesUsecase()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                println("ASHTEST:scc " + it)
                _foodEntries.postValue(it)
            }, {
                println("ASHTEST:ERR " + it)
            })
    }
}