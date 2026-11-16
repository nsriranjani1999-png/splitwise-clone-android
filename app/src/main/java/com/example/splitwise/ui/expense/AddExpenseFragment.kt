package com.example.splitwise.ui.expense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.splitwise.data.entity.UserEntity
import com.example.splitwise.databinding.FragmentAddExpenseBinding
import com.example.splitwise.ui.ViewModelFactory
import com.example.splitwise.util.parseToPaise
import com.example.splitwise.util.repository
import kotlinx.coroutines.launch

class AddExpenseFragment : Fragment() {

    private var _binding: FragmentAddExpenseBinding? = null
    private val binding get() = _binding!!

    private val groupId by lazy { requireArguments().getLong("groupId") }

    private val viewModel: AddExpenseViewModel by viewModels {
        ViewModelFactory(repository(), groupId)
    }

    private var members: List<UserEntity> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Load the group's members once, then fill the "Paid by" spinner and the checkboxes.
        viewLifecycleOwner.lifecycleScope.launch {
            members = viewModel.loadMembers()

            binding.spPaidBy.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                members.map { it.name }
            )

            members.forEach { member ->
                val checkBox = CheckBox(requireContext())
                checkBox.text = member.name
                checkBox.isChecked = true
                checkBox.tag = member.id
                binding.llParticipants.addView(checkBox)
            }
        }

        binding.btnSave.setOnClickListener { save() }
    }

    private fun save() {
        binding.tilDescription.error = null
        binding.tilAmount.error = null

        if (members.isEmpty()) return

        val description = binding.etDescription.text.toString().trim()
        val amountPaise = parseToPaise(binding.etAmount.text.toString())
        val paidBy = members[binding.spPaidBy.selectedItemPosition].id
        val participantIds = binding.llParticipants.children
            .filterIsInstance<CheckBox>()
            .filter { it.isChecked }
            .map { it.tag as Long }
            .toList()

        if (description.isEmpty()) {
            binding.tilDescription.error = "Enter a description"
            return
        }
        if (amountPaise == null || amountPaise <= 0) {
            binding.tilAmount.error = "Enter a valid amount"
            return
        }
        if (participantIds.isEmpty()) {
            Toast.makeText(requireContext(), "Pick at least one person to split with", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnSave.isEnabled = false
        viewModel.addExpense(description, amountPaise, paidBy, participantIds) {
            if (isAdded) findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
