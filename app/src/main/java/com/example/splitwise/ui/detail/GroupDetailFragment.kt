package com.example.splitwise.ui.detail

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
import com.example.splitwise.databinding.FragmentGroupDetailBinding
import com.example.splitwise.databinding.ItemBalanceBinding
import com.example.splitwise.databinding.ItemTransferBinding
import com.example.splitwise.ui.ViewModelFactory
import com.example.splitwise.util.formatRupees
import com.example.splitwise.util.repository
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class GroupDetailFragment : Fragment() {

    private var _binding: FragmentGroupDetailBinding? = null
    private val binding get() = _binding!!

    private val groupId by lazy { requireArguments().getLong("groupId") }

    private val viewModel: GroupDetailViewModel by viewModels {
        ViewModelFactory(repository(), groupId)
    }

    private val expensesAdapter = ExpensesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rvExpenses.adapter = expensesAdapter
        binding.rvExpenses.isNestedScrollingEnabled = false

        binding.fabAddExpense.setOnClickListener {
            findNavController().navigate(
                R.id.action_groupDetail_to_addExpense,
                bundleOf("groupId" to groupId)
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { render(it) }
            }
        }
    }

    private fun render(state: GroupDetailState) {
        // Balances: one line per member
        binding.balancesContainer.removeAllViews()
        state.balances.forEach { b ->
            val row = ItemBalanceBinding.inflate(layoutInflater, binding.balancesContainer, true)
            row.tvBalance.text = when {
                b.net > 0 -> "${b.name} gets back ${formatRupees(b.net)}"
                b.net < 0 -> "${b.name} owes ${formatRupees(-b.net)}"
                else -> "${b.name} is settled up"
            }
        }

        // Suggested payments, each with a Settle button
        binding.transfersContainer.removeAllViews()
        binding.tvTransfersTitle.isVisible = state.transfers.isNotEmpty()
        state.transfers.forEach { t ->
            val row = ItemTransferBinding.inflate(layoutInflater, binding.transfersContainer, true)
            row.tvTransfer.text = "${t.fromName} pays ${t.toName} ${formatRupees(t.amount)}"
            row.btnSettle.setOnClickListener { confirmSettle(t) }
        }

        // Expense list
        expensesAdapter.submitList(state.expenses)
        binding.tvNoExpenses.isVisible = state.expenses.isEmpty()
    }

    private fun confirmSettle(t: TransferUi) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Record payment?")
            .setMessage("${t.fromName} paid ${t.toName} ${formatRupees(t.amount)}")
            .setPositiveButton("Yes") { _, _ -> viewModel.settle(t) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
