package com.example.assignto_do.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.assignto_do.R
import com.example.assignto_do.databinding.ItemViewWorksFragmentBinding
import com.example.assignto_do.models.Works

class WorksAdapter(val onUnassignedBtnClicked: (Works) -> Unit) :
    RecyclerView.Adapter<WorksAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemViewWorksFragmentBinding) :
        RecyclerView.ViewHolder(binding.root)

    val diffUtil = object : DiffUtil.ItemCallback<Works>() {
        override fun areItemsTheSame(oldItem: Works, newItem: Works): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Works, newItem: Works): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemViewWorksFragmentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val works = differ.currentList[position]
        val isExpanded = works.expanded

        holder.binding.apply {
            titleTv.text = works.workTitle
            dateTv.text = works.workLastDate
            workDescriptionTv.text = works.workDesc

            when (works.workPriority) {
                "1" -> ovalIv.setImageResource(R.drawable.green_oval)
                "2" -> ovalIv.setImageResource(R.drawable.yellow_oval)
                "3" -> ovalIv.setImageResource(R.drawable.red_oval)
            }
            when (works.workStatus) {
                "1" -> holder.binding.statusTv.text = "Pending"
                "2" -> holder.binding.statusTv.text = "Progress"
                "3" -> holder.binding.statusTv.text = "Completed"
            }

            workLl.visibility = if (isExpanded) View.VISIBLE else View.GONE
            constraintLayout.setOnClickListener {
                isAnyItemExpanded(position)
                works.expanded = !works.expanded
                notifyItemChanged(position, 0)
            }

            unassignedBtn.setOnClickListener {
                onUnassignedBtnClicked(works)
            }

        }
    }

    private fun isAnyItemExpanded(position: Int) {
        val expandedItemIndex = differ.currentList.indexOfFirst { it.expanded }
        if (expandedItemIndex >= 0 && expandedItemIndex != position) {
            differ.currentList[expandedItemIndex].expanded = false
            notifyItemChanged(expandedItemIndex, 0)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {

        if (payloads.isNotEmpty() && payloads[0] == 0) {
            holder.binding.apply {
                workLl.visibility = View.GONE
            }
        }
        super.onBindViewHolder(holder, position, payloads)

    }


}