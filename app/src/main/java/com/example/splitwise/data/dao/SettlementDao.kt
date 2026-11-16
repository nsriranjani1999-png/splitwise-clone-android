package com.example.splitwise.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.splitwise.data.entity.SettlementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettlementDao {

    @Insert
    suspend fun insertSettlement(settlement: SettlementEntity): Long

    @Query("SELECT * FROM settlements WHERE groupId = :groupId ORDER BY createdAt DESC")
    fun getSettlementsForGroup(groupId: Long): Flow<List<SettlementEntity>>
}
