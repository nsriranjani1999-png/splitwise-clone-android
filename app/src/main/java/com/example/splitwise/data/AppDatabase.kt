package com.example.splitwise.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.splitwise.data.dao.ExpenseDao
import com.example.splitwise.data.dao.GroupDao
import com.example.splitwise.data.dao.SettlementDao
import com.example.splitwise.data.dao.UserDao
import com.example.splitwise.data.entity.ExpenseEntity
import com.example.splitwise.data.entity.ExpenseSplitEntity
import com.example.splitwise.data.entity.GroupEntity
import com.example.splitwise.data.entity.GroupMemberEntity
import com.example.splitwise.data.entity.SettlementEntity
import com.example.splitwise.data.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        GroupEntity::class,
        GroupMemberEntity::class,
        ExpenseEntity::class,
        ExpenseSplitEntity::class,
        SettlementEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun groupDao(): GroupDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun settlementDao(): SettlementDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // One database for the whole app.
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "splitwise.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
