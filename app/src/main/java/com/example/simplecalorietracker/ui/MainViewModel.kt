package com.example.simplecalorietracker.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplecalorietracker.domain.usecase.GetAuthTokenAndUserDetailsUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(val getAuthTokenAndUserDetailsUsecase: GetAuthTokenAndUserDetailsUsecase) :
    ViewModel() {

    private val _viewState = MutableLiveData<MainViewState>()
    val viewState: LiveData<MainViewState> = _viewState

    private val disposable = CompositeDisposable()

    fun getAuthToken() {
        _viewState.postValue(MainViewState.Loading)
        getAuthTokenAndUserDetailsUsecase()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                println("ASHTEST: user" + it.data)
                Timber.d("AuthToken fetch successful", it)
            }, {
                Timber.e("ERROR!! Fetching AuthToken", it)
            }).also { dis -> disposable.add(dis) }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}