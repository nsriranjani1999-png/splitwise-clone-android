package com.example.splitwise.domain

import com.example.splitwise.data.entity.ExpenseSplitEntity
import com.example.splitwise.data.entity.ExpenseWithPayer
import com.example.splitwise.data.entity.SettlementEntity

/** "fromUserId should pay toUserId this many paise." */
data class Transfer(val fromUserId: Long, val toUserId: Long, val amount: Long)

object BalanceCalculator {

    /**
     * net = Paid - Owes + Sent - Received   (all in paise, per user, for one group)
     *  > 0  -> the user is owed money
     *  < 0  -> the user owes money
     */
    fun netBalances(
        memberIds: List<Long>,
        expenses: List<ExpenseWithPayer>,
        splits: List<ExpenseSplitEntity>,
        settlements: List<SettlementEntity>
    ): Map<Long, Long> {
        val net = memberIds.associateWith { 0L }.toMutableMap()

        expenses.forEach { net[it.paidBy] = (net[it.paidBy] ?: 0L) + it.amount }          // Paid
        splits.forEach { net[it.userId] = (net[it.userId] ?: 0L) - it.amountOwed }        // Owes
        settlements.forEach {
            net[it.fromUser] = (net[it.fromUser] ?: 0L) + it.amount                       // Sent
            net[it.toUser] = (net[it.toUser] ?: 0L) - it.amount                           // Received
        }
        return net
    }

    /** Turns net balances into a short list of "A pays B" payments (greedy matching). */
    fun simplify(net: Map<Long, Long>): List<Transfer> {
        val creditors = net.filter { it.value > 0 }.entries
            .sortedByDescending { it.value }.map { it.key to it.value }.toMutableList()
        val debtors = net.filter { it.value < 0 }.entries
            .sortedBy { it.value }.map { it.key to -it.value }.toMutableList()

        val result = mutableListOf<Transfer>()
        var i = 0
        var j = 0
        while (i < debtors.size && j < creditors.size) {
            val (debtorId, owed) = debtors[i]
            val (creditorId, due) = creditors[j]
            val pay = minOf(owed, due)
            result.add(Transfer(debtorId, creditorId, pay))

            val owedLeft = owed - pay
            val dueLeft = due - pay
            debtors[i] = debtorId to owedLeft
            creditors[j] = creditorId to dueLeft
            if (owedLeft == 0L) i++
            if (dueLeft == 0L) j++
        }
        return result
    }
}
