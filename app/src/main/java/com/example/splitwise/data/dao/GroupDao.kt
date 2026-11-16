package com.example.splitwise.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.splitwise.data.entity.GroupEntity
import com.example.splitwise.data.entity.GroupMemberEntity
import com.example.splitwise.data.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {

    @Insert
    suspend fun insertGroup(group: GroupEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMember(member: GroupMemberEntity)

    @Query("SELECT * FROM groups")
    fun getAllGroups(): Flow<List<GroupEntity>>

    @Query(
        """
        SELECT users.* FROM users
        INNER JOIN group_members ON users.id = group_members.userId
        WHERE group_members.groupId = :groupId
        """
    )
    fun getMembers(groupId: Long): Flow<List<UserEntity>>

    @Delete
    suspend fun deleteGroup(group: GroupEntity)
}
