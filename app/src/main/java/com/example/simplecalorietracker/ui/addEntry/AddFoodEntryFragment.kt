package com.example.simplecalorietracker.ui.addEntry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.simplecalorietracker.databinding.FragmentAddFoodEntryBinding

class AddFoodEntryFragment : Fragment() {

    private var _binding: FragmentAddFoodEntryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val addFoodEntryViewModel = ViewModelProvider(this)[AddFoodEntryViewModel::class.java]
        _binding = FragmentAddFoodEntryBinding.inflate(inflater, container, false)

        addFoodEntryViewModel.text.observe(viewLifecycleOwner) {
        }

        binding.btnSubmit.setOnClickListener {
            //if success
            findNavController().popBackStack()
            //else show error toast
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}