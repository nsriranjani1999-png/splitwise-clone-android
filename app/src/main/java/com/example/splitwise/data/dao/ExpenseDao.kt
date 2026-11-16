package com.example.splitwise.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.splitwise.data.entity.ExpenseEntity
import com.example.splitwise.data.entity.ExpenseSplitEntity
import com.example.splitwise.data.entity.ExpenseWithPayer
import kotlinx.coroutines.flow.Flow

// abstract class (not interface) so it can contain the @Transaction function with a body.
@Dao
abstract class ExpenseDao {

    @Insert
    abstract suspend fun insertExpense(expense: ExpenseEntity): Long

    @Insert
    abstract suspend fun insertSplits(splits: List<ExpenseSplitEntity>)

    // Saves the expense and all its splits together, or none of them.
    @Transaction
    open suspend fun addExpenseWithSplits(
        expense: ExpenseEntity,
        splits: List<ExpenseSplitEntity>
    ) {
        val expenseId = insertExpense(expense)
        insertSplits(splits.map { it.copy(expenseId = expenseId) })
    }

    @Query(
        """
        SELECT expenses.id AS id,
               expenses.description AS description,
               expenses.amount AS amount,
               expenses.paidBy AS paidBy,
               expenses.createdAt AS createdAt,
               users.name AS payerName
        FROM expenses
        INNER JOIN users ON expenses.paidBy = users.id
        WHERE expenses.groupId = :groupId
        ORDER BY expenses.createdAt DESC
        """
    )
    abstract fun getExpensesWithPayer(groupId: Long): Flow<List<ExpenseWithPayer>>

    @Query(
        """
        SELECT expense_split.* FROM expense_split
        INNER JOIN expenses ON expense_split.expenseId = expenses.id
        WHERE expenses.groupId = :groupId
        """
    )
    abstract fun getSplitsForGroup(groupId: Long): Flow<List<ExpenseSplitEntity>>
}
