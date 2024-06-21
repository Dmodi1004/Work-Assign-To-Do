package com.example.assignto_do.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.assignto_do.databinding.ItemViewEmployeesProfileBinding
import com.example.assignto_do.models.Users
import com.example.assignto_do.views.fragments.EmployeesFragmentDirections

class EmployeesAdapter() : RecyclerView.Adapter<EmployeesAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemViewEmployeesProfileBinding) :
        RecyclerView.ViewHolder(binding.root)

    val diffUtil = object : DiffUtil.ItemCallback<Users>() {
        override fun areItemsTheSame(oldItem: Users, newItem: Users): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Users, newItem: Users): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemViewEmployeesProfileBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val empData = differ.currentList[position]

        holder.binding.apply {
            Glide.with(holder.itemView)
                .load(empData.userImage)
                .into(employeesProfileIv)
            employeesNameTv.text = empData.userName
        }
        holder.itemView.setOnClickListener {
            val action = EmployeesFragmentDirections.actionEmployeesFragmentToWorksFragment(empData)
            Navigation.findNavController(it)
                .navigate(action)
        }
    }


}