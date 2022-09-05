package com.example.simplecalorietracker.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplecalorietracker.databinding.FragmentUserHomeBinding
import com.example.simplecalorietracker.model.entity.FoodEntry
import com.example.simplecalorietracker.ui.user.adapter.UserFoodEntryAdapter

class UserHomeFragment : Fragment() {

    private var _binding: FragmentUserHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val userHomeViewModel = ViewModelProvider(this)[UserHomeViewModel::class.java]
        _binding = FragmentUserHomeBinding.inflate(inflater, container, false)

        userHomeViewModel.text.observe(viewLifecycleOwner) {
        }

        val foodEntry = FoodEntry(1, "12th Oct", "Chole Bhature", 2000)

        val foodEntryList = listOf(foodEntry, foodEntry, foodEntry, foodEntry, foodEntry)

        binding.rvFoodEntries.adapter = UserFoodEntryAdapter(foodEntryList)
        binding.rvFoodEntries.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}