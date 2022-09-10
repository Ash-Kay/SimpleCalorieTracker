package com.example.simplecalorietracker.ui.user

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplecalorietracker.R
import com.example.simplecalorietracker.data.entity.FoodEntryEntity
import com.example.simplecalorietracker.databinding.FragmentUserHomeBinding
import com.example.simplecalorietracker.ui.SharedViewModel
import com.example.simplecalorietracker.ui.user.adapter.UserFoodEntryAdapter
import com.example.simplecalorietracker.utils.CalendarRangeValidator
import com.example.simplecalorietracker.utils.Constants
import com.example.simplecalorietracker.utils.NetworkHandler
import com.example.simplecalorietracker.utils.toHumanDate
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserHomeFragment : Fragment() {

    @Inject
    lateinit var networkHandler: NetworkHandler

    private var _binding: FragmentUserHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: UserFoodEntryAdapter
    private lateinit var dateRangePicker: MaterialDatePicker<Pair<Long, Long>>

    private val viewModel: UserHomeViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserHomeBinding.inflate(inflater, container, false)

        setupDatePicker()

        binding.fab.setOnClickListener {
            sharedViewModel.updateItem(null)
            findNavController().navigate(R.id.action_userHomeFragment_to_addFoodEntryFragment)
        }

        binding.srlHomeRoot.setOnRefreshListener {
            clearFilterAndFetch()
        }

        dateRangePicker.addOnPositiveButtonClickListener {
            //Added one day to make it till EOD
            if (networkHandler.isNetworkAvailable()) {
                binding.llFilter.visibility = View.VISIBLE
                val endDatePlusOneDay = it.second + Constants.oneDayInMillis - 1
                binding.tvFilterDetails.text =
                    "Filter: ${it.first.toHumanDate()} - ${endDatePlusOneDay.toHumanDate()}"
                checkInternetAndGetFoodEntries(it.first, endDatePlusOneDay)
            } else {
                viewModel.showNoInternetError()
            }
        }

        binding.btnClearFilter.setOnClickListener {
            clearFilterAndFetch()
        }

        adapter = UserFoodEntryAdapter(::itemUpdateClicked, ::itemDeleteClicked)
        binding.rvFoodEntries.adapter = adapter
        binding.rvFoodEntries.layoutManager = LinearLayoutManager(context)

        viewModel.viewState.observe(viewLifecycleOwner) {
            renderViewState(it)
        }

        viewModel.todayConsumption.observe(viewLifecycleOwner) {
            binding.consumptionStat.text = "$it / ${Constants.dailyCalorieLimit}"

            if (it <= Constants.dailyCalorieLimit) {
                binding.llDailyLimit.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.greenTint
                    )
                )
            } else {
                binding.llDailyLimit.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.redTint
                    )
                )
            }
        }

        return binding.root
    }

    private fun clearFilterAndFetch() {
        binding.llFilter.visibility = View.GONE
        checkInternetAndGetFoodEntries()
    }

    private fun itemUpdateClicked(foodEntryEntity: FoodEntryEntity) {
        sharedViewModel.updateItem(foodEntryEntity)
        findNavController().navigate(R.id.action_userHomeFragment_to_addFoodEntryFragment)
    }

    private fun itemDeleteClicked(foodEntryEntity: FoodEntryEntity) {
        if (networkHandler.isNetworkAvailable()) {
            viewModel.deleteFoodEntry(foodEntryEntity)
        } else {
            viewModel.showNoInternetError()
        }
    }

    private fun renderViewState(state: UserHomeViewState) {
        when (state) {
            UserHomeViewState.Idle -> {
                checkInternetAndGetFoodEntries()
            }
            UserHomeViewState.Loading -> {
                binding.srlHomeRoot.isRefreshing = true
            }
            is UserHomeViewState.DataFetchSuccess -> {
                updateUiWithNewData(state.foodEntries)
            }
            is UserHomeViewState.CacheDataFetchSuccess -> {
                updateUiWithNewData(state.foodEntries)
            }
            is UserHomeViewState.Error -> {
                binding.srlHomeRoot.isRefreshing = false
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.getCacheFoodEntries()
            }
        }
    }

    private fun updateUiWithNewData(foodEntries: List<FoodEntryEntity>) {
        binding.srlHomeRoot.isRefreshing = false
        adapter.updateFoodEntryList(foodEntries)
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

    private fun checkInternetAndGetFoodEntries(start: Long = 0, end: Long = 0) {
        if (networkHandler.isNetworkAvailable()) {
            viewModel.getFoodEntries(start, end)
        } else {
            viewModel.showNoInternetError()
        }
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