package com.example.splitwise.data

import com.example.splitwise.data.dao.ExpenseDao
import com.example.splitwise.data.dao.GroupDao
import com.example.splitwise.data.dao.SettlementDao
import com.example.splitwise.data.dao.UserDao
import com.example.splitwise.data.entity.ExpenseEntity
import com.example.splitwise.data.entity.ExpenseSplitEntity
import com.example.splitwise.data.entity.ExpenseWithPayer
import com.example.splitwise.data.entity.GroupEntity
import com.example.splitwise.data.entity.GroupMemberEntity
import com.example.splitwise.data.entity.SettlementEntity
import com.example.splitwise.data.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * The only class the ViewModels talk to. It owns the DAOs and hides where data comes from.
 */
class SplitwiseRepository(
    private val userDao: UserDao,
    private val groupDao: GroupDao,
    private val expenseDao: ExpenseDao,
    private val settlementDao: SettlementDao
) {

    // ---------- Groups ----------
    val allGroups: Flow<List<GroupEntity>> = groupDao.getAllGroups()

    fun getMembers(groupId: Long): Flow<List<UserEntity>> = groupDao.getMembers(groupId)

    /** Creates the group, then adds each member (re-using an existing user with the same name). */
    suspend fun createGroup(name: String, memberNames: List<String>): Long {
        val groupId = groupDao.insertGroup(GroupEntity(name = name.trim()))
        memberNames
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinctBy { it.lowercase() }
            .forEach { memberName ->
                val userId = userDao.findByName(memberName)?.id
                    ?: userDao.insert(UserEntity(name = memberName))
                groupDao.addMember(GroupMemberEntity(groupId = groupId, userId = userId))
            }
        return groupId
    }

    suspend fun deleteGroup(group: GroupEntity) = groupDao.deleteGroup(group)

    // ---------- Expenses ----------
    fun getExpenses(groupId: Long): Flow<List<ExpenseWithPayer>> =
        expenseDao.getExpensesWithPayer(groupId)

    fun getSplits(groupId: Long): Flow<List<ExpenseSplitEntity>> =
        expenseDao.getSplitsForGroup(groupId)

    suspend fun addExpense(
        groupId: Long,
        description: String,
        amountPaise: Long,
        paidBy: Long,
        participantIds: List<Long>
    ) {
        require(participantIds.isNotEmpty()) { "Pick at least one person to split with" }
        val expense = ExpenseEntity(
            groupId = groupId,
            description = description.trim(),
            amount = amountPaise,
            paidBy = paidBy,
            createdAt = System.currentTimeMillis()
        )
        expenseDao.addExpenseWithSplits(expense, splitEqually(amountPaise, participantIds))
    }

    /**
     * Equal split in whole paise. The leftover paise (amount % people) go one each
     * to the first few people, so the shares always add up to the full amount.
     */
    private fun splitEqually(amountPaise: Long, userIds: List<Long>): List<ExpenseSplitEntity> {
        val count = userIds.size
        val share = amountPaise / count
        val leftover = (amountPaise % count).toInt()
        return userIds.mapIndexed { index, userId ->
            val extra = if (index < leftover) 1L else 0L
            ExpenseSplitEntity(expenseId = 0, userId = userId, amountOwed = share + extra)
        }
    }

    // ---------- Settlements ----------
    fun getSettlements(groupId: Long): Flow<List<SettlementEntity>> =
        settlementDao.getSettlementsForGroup(groupId)

    suspend fun settle(groupId: Long, fromUser: Long, toUser: Long, amountPaise: Long) {
        settlementDao.insertSettlement(
            SettlementEntity(
                groupId = groupId,
                fromUser = fromUser,
                toUser = toUser,
                amount = amountPaise,
                createdAt = System.currentTimeMillis()
            )
        )
    }
}
