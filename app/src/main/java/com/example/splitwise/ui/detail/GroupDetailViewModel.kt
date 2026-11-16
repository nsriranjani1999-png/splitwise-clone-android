package com.example.splitwise.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitwise.data.SplitwiseRepository
import com.example.splitwise.data.entity.ExpenseWithPayer
import com.example.splitwise.domain.BalanceCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class MemberBalance(val userId: Long, val name: String, val net: Long)

data class TransferUi(
    val fromId: Long,
    val fromName: String,
    val toId: Long,
    val toName: String,
    val amount: Long
)

data class GroupDetailState(
    val balances: List<MemberBalance>,
    val transfers: List<TransferUi>,
    val expenses: List<ExpenseWithPayer>
)

class GroupDetailViewModel(
    private val repo: SplitwiseRepository,
    private val groupId: Long
) : ViewModel() {

    // Recalculated automatically whenever members, expenses, splits or settlements change.
    val state: Flow<GroupDetailState> = combine(
        repo.getMembers(groupId),
        repo.getExpenses(groupId),
        repo.getSplits(groupId),
        repo.getSettlements(groupId)
    ) { members, expenses, splits, settlements ->
        val names = members.associate { it.id to it.name }
        val net = BalanceCalculator.netBalances(members.map { it.id }, expenses, splits, settlements)
        val transfers = BalanceCalculator.simplify(net)

        GroupDetailState(
            balances = members.map { MemberBalance(it.id, it.name, net[it.id] ?: 0L) },
            transfers = transfers.map {
                TransferUi(
                    fromId = it.fromUserId,
                    fromName = names[it.fromUserId] ?: "?",
                    toId = it.toUserId,
                    toName = names[it.toUserId] ?: "?",
                    amount = it.amount
                )
            },
            expenses = expenses
        )
    }

    fun settle(transfer: TransferUi) {
        viewModelScope.launch {
            repo.settle(groupId, transfer.fromId, transfer.toId, transfer.amount)
        }
    }
}
