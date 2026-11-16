package com.example.splitwise.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Table is "groups" because GROUP is a reserved word in SQL.
@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)
