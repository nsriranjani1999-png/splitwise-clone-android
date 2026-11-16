package com.example.splitwise

import android.app.Application
import com.example.splitwise.data.AppDatabase
import com.example.splitwise.data.SplitwiseRepository

class SplitwiseApp : Application() {

    val repository: SplitwiseRepository by lazy {
        val db = AppDatabase.getInstance(this)
        SplitwiseRepository(db.userDao(), db.groupDao(), db.expenseDao(), db.settlementDao())
    }
}
