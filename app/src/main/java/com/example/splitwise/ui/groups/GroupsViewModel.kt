package com.example.splitwise.ui.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitwise.data.SplitwiseRepository
import com.example.splitwise.data.entity.GroupEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class GroupsViewModel(private val repo: SplitwiseRepository) : ViewModel() {

    val groups: Flow<List<GroupEntity>> = repo.allGroups

    fun createGroup(name: String, memberNames: List<String>, onDone: () -> Unit) {
        viewModelScope.launch {
            repo.createGroup(name, memberNames)
            onDone()
        }
    }

    fun deleteGroup(group: GroupEntity) {
        viewModelScope.launch { repo.deleteGroup(group) }
    }
}
