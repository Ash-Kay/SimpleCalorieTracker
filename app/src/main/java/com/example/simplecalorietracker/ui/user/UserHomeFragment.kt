package com.example.simplecalorietracker.ui.user

import android.os.Bundle
import android.view.*
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplecalorietracker.R
import com.example.simplecalorietracker.databinding.FragmentUserHomeBinding
import com.example.simplecalorietracker.ui.user.adapter.UserFoodEntryAdapter
import com.example.simplecalorietracker.utils.CalendarRangeValidator
import com.example.simplecalorietracker.utils.Constants
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserHomeFragment : Fragment() {

    private var _binding: FragmentUserHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: UserFoodEntryAdapter
    private lateinit var dateRangePicker: MaterialDatePicker<Pair<Long, Long>>

    private val viewModel: UserHomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel.getFoodEntries()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserHomeBinding.inflate(inflater, container, false)

        setupDatePicker()

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_userHomeFragment_to_addFoodEntryFragment)
        }

        binding.srlHomeRoot.setOnRefreshListener {
            //TODO: if offline cancel loader
            viewModel.getFoodEntries()
        }

        dateRangePicker.addOnPositiveButtonClickListener {
            //Added one day to make it till EOD
            viewModel.getFoodEntries(it.first, it.second + Constants.oneDayInMillis)
        }

//        dateRangePicker.addOnNegativeButtonClickListener {
//            viewModel.getFoodEntries(0, 0)
//        }

        adapter = UserFoodEntryAdapter()
        binding.rvFoodEntries.adapter = adapter
        binding.rvFoodEntries.layoutManager = LinearLayoutManager(context)

        viewModel.foodEntries.observe(viewLifecycleOwner) {
            if (binding.srlHomeRoot.isRefreshing) {
                binding.srlHomeRoot.isRefreshing = false
            }
            adapter.updateFoodEntryList(it)
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.filter) {
            dateRangePicker.show(parentFragmentManager, "DATE_PICKER_RANGE")
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupDatePicker() {
        dateRangePicker = MaterialDatePicker
            .Builder
            .dateRangePicker()
            .setCalendarConstraints(setupCalenderConstraint())
            .setTitleText("Select a date range")
            .build()
    }

    private fun setupCalenderConstraint(): CalendarConstraints {
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val last3year = MaterialDatePicker.todayInUtcMilliseconds() - Constants.threeYearsInMillis

        return CalendarConstraints.Builder()
            .setOpenAt(today)
            .setStart(last3year)
            .setEnd(today)
            .setValidator(CalendarRangeValidator(last3year, today))
            .build()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}