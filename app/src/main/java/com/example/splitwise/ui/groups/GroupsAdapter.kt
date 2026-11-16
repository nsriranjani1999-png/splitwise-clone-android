package com.example.splitwise.ui.groups

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.splitwise.data.entity.GroupEntity
import com.example.splitwise.databinding.ItemGroupBinding

private object GroupDiff : DiffUtil.ItemCallback<GroupEntity>() {
    override fun areItemsTheSame(oldItem: GroupEntity, newItem: GroupEntity) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: GroupEntity, newItem: GroupEntity) = oldItem == newItem
}

class GroupsAdapter(
    private val onClick: (GroupEntity) -> Unit,
    private val onLongClick: (GroupEntity) -> Unit
) : ListAdapter<GroupEntity, GroupsAdapter.VH>(GroupDiff) {

    class VH(val binding: ItemGroupBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val group = getItem(position)
        holder.binding.tvGroupName.text = group.name
        holder.binding.root.setOnClickListener { onClick(group) }
        holder.binding.root.setOnLongClickListener {
            onLongClick(group)
            true
        }
    }
}
