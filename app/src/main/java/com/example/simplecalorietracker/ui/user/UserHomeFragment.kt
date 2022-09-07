package com.example.simplecalorietracker.ui.user

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplecalorietracker.R
import com.example.simplecalorietracker.databinding.FragmentUserHomeBinding
import com.example.simplecalorietracker.model.entity.FoodEntry
import com.example.simplecalorietracker.ui.user.adapter.UserFoodEntryAdapter

class UserHomeFragment : Fragment() {

    private var _binding: FragmentUserHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val userHomeViewModel = ViewModelProvider(this)[UserHomeViewModel::class.java]
        _binding = FragmentUserHomeBinding.inflate(inflater, container, false)

        userHomeViewModel.text.observe(viewLifecycleOwner) {
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_userHomeFragment_to_addFoodEntryFragment)
        }

        val foodEntry = FoodEntry(1, "12th Oct", "Chole Bhature", 2000)
        val foodEntry1 = FoodEntry(2, "13th Oct", "Chole Bhature Special pro max ultra", 200000000000000)
        val foodEntryList = listOf(foodEntry, foodEntry1, foodEntry, foodEntry, foodEntry)

        binding.rvFoodEntries.adapter = UserFoodEntryAdapter(foodEntryList)
        binding.rvFoodEntries.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}