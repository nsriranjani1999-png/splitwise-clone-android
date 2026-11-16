package com.example.splitwise.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.splitwise.data.SplitwiseRepository
import com.example.splitwise.ui.detail.GroupDetailViewModel
import com.example.splitwise.ui.expense.AddExpenseViewModel
import com.example.splitwise.ui.groups.GroupsViewModel

/** Builds ViewModels that need constructor arguments (the repository, and sometimes a groupId). */
class ViewModelFactory(
    private val repo: SplitwiseRepository,
    private val groupId: Long = 0L
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(GroupsViewModel::class.java) -> GroupsViewModel(repo) as T
        modelClass.isAssignableFrom(GroupDetailViewModel::class.java) -> GroupDetailViewModel(repo, groupId) as T
        modelClass.isAssignableFrom(AddExpenseViewModel::class.java) -> AddExpenseViewModel(repo, groupId) as T
        else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}
