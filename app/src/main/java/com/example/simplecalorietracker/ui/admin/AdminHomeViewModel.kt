package com.example.simplecalorietracker.ui.admin

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class AdminHomeViewModel @Inject constructor(
) : ViewModel() {

    private val disposable = CompositeDisposable()


    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}