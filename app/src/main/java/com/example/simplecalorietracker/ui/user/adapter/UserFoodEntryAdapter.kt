package com.example.simplecalorietracker.ui.user.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.simplecalorietracker.databinding.ItemFoodEntryBinding
import com.example.simplecalorietracker.model.entity.FoodEntry

class UserFoodEntryAdapter(private val courseList: List<FoodEntry>) :
    RecyclerView.Adapter<UserFoodEntryAdapter.UserFoodEntryViewHolder>() {
    lateinit var binding: ItemFoodEntryBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserFoodEntryViewHolder {
        binding = ItemFoodEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserFoodEntryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserFoodEntryViewHolder, position: Int) {
        holder.bind(courseList[position])
    }

    override fun getItemCount() = courseList.size

    class UserFoodEntryViewHolder(val binding: ItemFoodEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(foodEntry: FoodEntry) {
            with(binding) {
                tvDate.text = foodEntry.date
                tvFoodName.text = foodEntry.foodName
                tvCalorieCount.text = foodEntry.calorieCount.toString()
            }
        }
    }
}