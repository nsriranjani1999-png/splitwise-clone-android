package com.example.splitwise.data.entity

// Not an @Entity. It only holds the result of the expenses JOIN users query.
data class ExpenseWithPayer(
    val id: Long,
    val description: String,
    val amount: Long,
    val paidBy: Long,
    val createdAt: Long,
    val payerName: String
)
