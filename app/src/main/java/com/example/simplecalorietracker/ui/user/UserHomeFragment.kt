package com.example.simplecalorietracker.ui.user

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplecalorietracker.R
import com.example.simplecalorietracker.databinding.FragmentUserHomeBinding
import com.example.simplecalorietracker.ui.user.adapter.UserFoodEntryAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserHomeFragment : Fragment() {

    private var _binding: FragmentUserHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: UserFoodEntryAdapter

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

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_userHomeFragment_to_addFoodEntryFragment)
        }

        binding.srlHomeRoot.setOnRefreshListener {
            viewModel.getFoodEntries()
        }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}