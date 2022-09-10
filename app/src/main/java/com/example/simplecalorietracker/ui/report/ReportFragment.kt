package com.example.simplecalorietracker.ui.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.simplecalorietracker.databinding.FragmentReportBinding
import dagger.hilt.android.AndroidEntryPoint
import java.math.RoundingMode
import java.text.DecimalFormat

@AndroidEntryPoint
class ReportFragment : Fragment() {
    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReportViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)

        viewModel.viewState.observe(viewLifecycleOwner) {
            renderViewState(it)
        }

        return binding.root
    }

    private fun renderViewState(state: ReportViewState) {
        when (state) {
            ReportViewState.Idle -> {
                viewModel.fetchReport()
            }
            ReportViewState.Loading -> {
                //Show loading ui
            }
            is ReportViewState.Success -> {
                binding.entriesCurrent.text = state.response.currentWeekEntryCount.toString()
                binding.entriesLast.text = state.response.lastWeekEntryCount.toString()
                val df = DecimalFormat("#.##")
                df.roundingMode = RoundingMode.CEILING
                binding.avgCalorie.text = df.format(state.response.currentWeekAvgEntry)
            }
            is ReportViewState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}