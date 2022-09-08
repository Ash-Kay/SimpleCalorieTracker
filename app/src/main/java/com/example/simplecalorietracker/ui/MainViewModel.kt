package com.example.simplecalorietracker.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplecalorietracker.domain.usecase.GetAuthTokenUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(val authTokenUsecase: GetAuthTokenUsecase) : ViewModel() {

    private val _authToken = MutableLiveData<String?>()
    val authToken: LiveData<String?> = _authToken
    private val disposable = CompositeDisposable()

    fun getAuthToken() {
        authTokenUsecase()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _authToken.postValue(it.data.token)
                Timber.d("AuthToken fetch successful", it)
            }, {
                _authToken.postValue(null)
                Timber.e("ERROR!! Fetching AuthToken", it)
            }).also { dis -> disposable.add(dis) }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}