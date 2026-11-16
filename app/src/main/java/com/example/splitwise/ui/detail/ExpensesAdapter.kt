package com.example.splitwise.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.splitwise.data.entity.ExpenseWithPayer
import com.example.splitwise.databinding.ItemExpenseBinding
import com.example.splitwise.util.formatRupees
import java.text.DateFormat
import java.util.Date

private object ExpenseDiff : DiffUtil.ItemCallback<ExpenseWithPayer>() {
    override fun areItemsTheSame(oldItem: ExpenseWithPayer, newItem: ExpenseWithPayer) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: ExpenseWithPayer, newItem: ExpenseWithPayer) = oldItem == newItem
}

class ExpensesAdapter : ListAdapter<ExpenseWithPayer, ExpensesAdapter.VH>(ExpenseDiff) {

    class VH(val binding: ItemExpenseBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val expense = getItem(position)
        val date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(expense.createdAt))
        holder.binding.tvDescription.text = expense.description
        holder.binding.tvSubtitle.text = "Paid by ${expense.payerName} • $date"
        holder.binding.tvAmount.text = formatRupees(expense.amount)
    }
}
