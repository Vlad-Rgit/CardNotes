package com.example.cardnotes.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cardnotes.databinding.GroupItemBinding
import com.example.cardnotes.domain.GroupDomain
import com.example.cardnotes.utils.replaceAll

class GroupsAdapter(
    context: Context)
    : RecyclerView.Adapter<GroupsAdapter.ViewHolder>() {


    private val inflater = LayoutInflater.from(context)
    private val groups = mutableListOf<GroupDomain>()

    private var groupClickedCallback:
            ((group: GroupDomain, position: Int) -> Unit)? = null


    fun replaceAll(items: List<GroupDomain>) {
        groups.replaceAll(items, this)
    }

    fun setGroupClickedCallback(callback:
                                    (group: GroupDomain, position: Int) -> Unit) {
        groupClickedCallback = callback
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = GroupItemBinding.inflate(
            inflater, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val group = groups[position]
        holder.performBind(group)
    }

    override fun getItemCount(): Int {
        return groups.size
    }


    inner class ViewHolder(
        private val binding: GroupItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                groupClickedCallback?.invoke(binding.group, layoutPosition)
            }
        }

        fun performBind(group: GroupDomain) {
            binding.group = group
        }

    }

}