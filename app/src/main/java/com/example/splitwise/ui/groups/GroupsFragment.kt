package com.example.splitwise.ui.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.splitwise.R
import com.example.splitwise.data.entity.GroupEntity
import com.example.splitwise.databinding.FragmentGroupsBinding
import com.example.splitwise.ui.ViewModelFactory
import com.example.splitwise.util.repository
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class GroupsFragment : Fragment() {

    private var _binding: FragmentGroupsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GroupsViewModel by viewModels { ViewModelFactory(repository()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = GroupsAdapter(
            onClick = { group ->
                findNavController().navigate(
                    R.id.action_groups_to_groupDetail,
                    bundleOf("groupId" to group.id, "groupName" to group.name)
                )
            },
            onLongClick = { group -> confirmDelete(group) }
        )
        binding.rvGroups.adapter = adapter

        binding.fabAddGroup.setOnClickListener {
            findNavController().navigate(R.id.action_groups_to_createGroup)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.groups.collect { groups ->
                    adapter.submitList(groups)
                    binding.tvEmpty.isVisible = groups.isEmpty()
                }
            }
        }
    }

    private fun confirmDelete(group: GroupEntity) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete group?")
            .setMessage("\"${group.name}\" and all its expenses and payments will be deleted.")
            .setPositiveButton("Delete") { _, _ -> viewModel.deleteGroup(group) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
