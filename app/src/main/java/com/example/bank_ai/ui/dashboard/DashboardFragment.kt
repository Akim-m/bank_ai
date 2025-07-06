package com.example.bank_ai.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bank_ai.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: DashboardViewModel
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        
        return root
    }
    
    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter()
        binding.transactionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionAdapter
        }
    }
    
    private fun setupObservers() {
        viewModel.checkingBalance.observe(viewLifecycleOwner) { balance ->
            binding.checkingBalance.text = balance
        }
        
        viewModel.savingsBalance.observe(viewLifecycleOwner) { balance ->
            binding.savingsBalance.text = balance
        }
        
        viewModel.creditBalance.observe(viewLifecycleOwner) { balance ->
            binding.creditBalance.text = balance
        }
        
        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            transactionAdapter.updateTransactions(transactions)
        }
    }
    
    private fun setupClickListeners() {
        binding.transferCard.setOnClickListener {
            Toast.makeText(context, "Transfer Money", Toast.LENGTH_SHORT).show()
        }
        
        binding.payBillsCard.setOnClickListener {
            Toast.makeText(context, "Pay Bills", Toast.LENGTH_SHORT).show()
        }
        
        binding.depositCard.setOnClickListener {
            Toast.makeText(context, "Deposit Check", Toast.LENGTH_SHORT).show()
        }
        
        binding.statementsCard.setOnClickListener {
            Toast.makeText(context, "View Statements", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}