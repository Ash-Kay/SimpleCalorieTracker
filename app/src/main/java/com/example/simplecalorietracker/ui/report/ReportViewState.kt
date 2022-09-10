package com.example.simplecalorietracker.ui.report

import com.example.simplecalorietracker.data.remote.GetReportResponse

sealed class ReportViewState {
    object Idle : ReportViewState()
    object Loading : ReportViewState()
    data class Error(val message: String) : ReportViewState()
    data class Success(val response: GetReportResponse) : ReportViewState()
}