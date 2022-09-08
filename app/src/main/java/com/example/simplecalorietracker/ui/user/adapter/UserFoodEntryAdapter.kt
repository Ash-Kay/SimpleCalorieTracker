package com.example.simplecalorietracker.ui.user.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.simplecalorietracker.R
import com.example.simplecalorietracker.databinding.ItemFoodEntryBinding
import com.example.simplecalorietracker.data.entity.FoodEntryEntity
import kotlin.math.ln
import kotlin.math.pow

class UserFoodEntryAdapter : RecyclerView.Adapter<UserFoodEntryAdapter.UserFoodEntryViewHolder>() {
    private lateinit var binding: ItemFoodEntryBinding
    private val foodEntryList: MutableList<FoodEntryEntity> = mutableListOf()

    fun updateFoodEntryList(list: List<FoodEntryEntity>) {
        foodEntryList.clear()
        foodEntryList.addAll(list)
        //TODO: CHECK
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserFoodEntryViewHolder {
        binding = ItemFoodEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserFoodEntryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserFoodEntryViewHolder, position: Int) {
        holder.bind(foodEntryList[position])
    }

    override fun getItemCount() = foodEntryList.size

    class UserFoodEntryViewHolder(private val binding: ItemFoodEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(foodEntry: FoodEntryEntity) {
            with(binding) {
                //TODO: change to readable
                tvDate.text = foodEntry.timestamp.toString()
                tvFoodName.text = foodEntry.name
                tvCalorieCount.text = compactNumber(foodEntry.calorie)

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

        private fun compactNumber(number: Long): String {
            if (number < 1000) return "" + number
            val exp = (ln(number.toDouble()) / ln(1000.0)).toInt()
            return String.format(
                "%.1f %cCal", number / 1000.0.pow(exp.toDouble()),
                "kMGTPE"[exp - 1]
            )
        }
    }
}