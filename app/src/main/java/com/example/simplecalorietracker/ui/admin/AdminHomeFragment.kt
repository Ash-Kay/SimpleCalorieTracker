package com.example.simplecalorietracker.ui.admin

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplecalorietracker.R
import com.example.simplecalorietracker.data.entity.FoodEntryEntity
import com.example.simplecalorietracker.databinding.FragmentAdminHomeBinding
import com.example.simplecalorietracker.ui.SharedViewModel
import com.example.simplecalorietracker.ui.user.UserHomeViewState
import com.example.simplecalorietracker.ui.user.adapter.UserFoodEntryAdapter
import com.example.simplecalorietracker.utils.Constants
import com.example.simplecalorietracker.utils.NetworkHandler
import com.example.simplecalorietracker.utils.setupCalenderConstraint
import com.example.simplecalorietracker.utils.toHumanDate
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AdminHomeFragment : Fragment() {

    @Inject
    lateinit var networkHandler: NetworkHandler

    private var _binding: FragmentAdminHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: UserFoodEntryAdapter
    private lateinit var dateRangePicker: MaterialDatePicker<Pair<Long, Long>>

    private val viewModel: AdminHomeViewModel by viewModels()
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
        _binding = FragmentAdminHomeBinding.inflate(inflater, container, false)

        setupDatePicker()

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
        binding.rvUsersFoodEntries.adapter = adapter
        binding.rvUsersFoodEntries.layoutManager = LinearLayoutManager(context)

        viewModel.viewState.observe(viewLifecycleOwner) {
            renderViewState(it)
        }

        return binding.root
    }

    private fun renderViewState(state: AdminHomeViewState) {
        when (state) {
            AdminHomeViewState.Idle -> {
                checkInternetAndGetFoodEntries()
            }
            AdminHomeViewState.Loading -> {
                binding.srlHomeRoot.isRefreshing = true
            }
            is AdminHomeViewState.DataFetchSuccess -> {
                updateUiWithNewData(state.foodEntries)
            }
            is AdminHomeViewState.CacheDataFetchSuccess -> {
                updateUiWithNewData(state.foodEntries)
            }
            is AdminHomeViewState.Error -> {
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
        inflater.inflate(R.menu.admin_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.filter) {
            dateRangePicker.show(parentFragmentManager, "DATE_PICKER_RANGE_ADMIN")
        } else if (item.itemId == R.id.report) {
            //TODO: Add
        }
        return super.onOptionsItemSelected(item)
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

    private fun clearFilterAndFetch() {
        binding.llFilter.visibility = View.GONE
        checkInternetAndGetFoodEntries()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}