package com.example.simplecalorietracker.ui.user.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.simplecalorietracker.R
import com.example.simplecalorietracker.databinding.ItemFoodEntryBinding
import com.example.simplecalorietracker.model.entity.FoodEntry

class UserFoodEntryAdapter(private val courseList: List<FoodEntry>) :
    RecyclerView.Adapter<UserFoodEntryAdapter.UserFoodEntryViewHolder>() {
    private lateinit var binding: ItemFoodEntryBinding

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

                btnMoreOption.setOnClickListener {
                    val popup = PopupMenu(binding.root.context, btnMoreOption)
                    popup.inflate(R.menu.item_edit_menu)
                    popup.setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.edit -> {
                                Toast.makeText(binding.root.context, "EDIT!", Toast.LENGTH_LONG)
                                    .show()
                            }
                            R.id.delete -> {
                                Toast.makeText(binding.root.context, "DELETE!", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                        return@setOnMenuItemClickListener true
                    }
                    popup.show()
                }
            }
        }
    }
}