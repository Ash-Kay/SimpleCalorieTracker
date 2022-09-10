package com.example.simplecalorietracker.ui.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplecalorietracker.domain.usecase.GetReportUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    val getReportUsecase: GetReportUsecase
) : ViewModel() {

    private val _viewState = MutableLiveData<ReportViewState>(ReportViewState.Idle)
    val viewState: LiveData<ReportViewState> = _viewState

    private val disposable = CompositeDisposable()

    fun fetchReport(){
        _viewState.postValue(ReportViewState.Loading)
        getReportUsecase()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _viewState.postValue(ReportViewState.Success(it))
                Timber.d("Report fetch successful", it)
            }, {
                _viewState.postValue(ReportViewState.Error("Error fetching data"))
                Timber.e("ERROR!! Fetching Report", it)
            }).also { dis -> disposable.add(dis) }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}