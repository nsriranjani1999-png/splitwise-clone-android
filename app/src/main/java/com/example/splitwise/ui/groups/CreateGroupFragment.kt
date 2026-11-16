package com.example.splitwise.ui.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.splitwise.databinding.FragmentCreateGroupBinding
import com.example.splitwise.ui.ViewModelFactory
import com.example.splitwise.util.repository

class CreateGroupFragment : Fragment() {

    private var _binding: FragmentCreateGroupBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GroupsViewModel by viewModels { ViewModelFactory(repository()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnCreate.setOnClickListener { createGroup() }
    }

    private fun createGroup() {
        binding.tilGroupName.error = null
        binding.tilMembers.error = null

        val name = binding.etGroupName.text.toString().trim()
        val members = binding.etMembers.text.toString()
            .split(",", "\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinctBy { it.lowercase() }

        if (name.isEmpty()) {
            binding.tilGroupName.error = "Enter a group name"
            return
        }
        if (members.size < 2) {
            binding.tilMembers.error = "Add at least two people, separated by commas"
            return
        }

        binding.btnCreate.isEnabled = false
        viewModel.createGroup(name, members) {
            if (isAdded) findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
