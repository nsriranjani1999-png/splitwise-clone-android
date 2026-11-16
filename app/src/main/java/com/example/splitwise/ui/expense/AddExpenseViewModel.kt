package com.example.splitwise.ui.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitwise.data.SplitwiseRepository
import com.example.splitwise.data.entity.UserEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AddExpenseViewModel(
    private val repo: SplitwiseRepository,
    private val groupId: Long
) : ViewModel() {

    suspend fun loadMembers(): List<UserEntity> = repo.getMembers(groupId).first()

    fun addExpense(
        description: String,
        amountPaise: Long,
        paidBy: Long,
        participantIds: List<Long>,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            repo.addExpense(groupId, description, amountPaise, paidBy, participantIds)
            onDone()
        }
    }
}
