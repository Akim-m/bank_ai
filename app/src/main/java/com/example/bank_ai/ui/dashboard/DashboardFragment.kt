package com.example.bank_ai.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bank_ai.databinding.FragmentDashboardBinding
import com.example.bank_ai.ui.sms.SmsViewModel

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: DashboardViewModel
    private val smsViewModel: SmsViewModel by activityViewModels()
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
        observeSmsMessages()
        
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
    
    private fun observeSmsMessages() {
        smsViewModel.messages.observe(viewLifecycleOwner) { smsMessages ->
            viewModel.updateFromSms(smsMessages)
        }
    }
    
    private fun setupClickListeners() {
        binding.transferAction.setOnClickListener {
            Toast.makeText(context, "Transfer Money (Demo)", Toast.LENGTH_SHORT).show()
        }
        binding.payBillsAction.setOnClickListener {
            Toast.makeText(context, "Pay Bills (Demo)", Toast.LENGTH_SHORT).show()
        }
        binding.depositAction.setOnClickListener {
            Toast.makeText(context, "Deposit Check (Demo)", Toast.LENGTH_SHORT).show()
        }
        binding.statementsAction.setOnClickListener {
            Toast.makeText(context, "View Statements (Demo)", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}