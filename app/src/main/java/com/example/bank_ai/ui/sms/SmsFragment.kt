package com.example.bank_ai.ui.sms

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bank_ai.R
import com.example.bank_ai.databinding.FragmentSmsBinding

class SmsFragment : Fragment() {

    private var _binding: FragmentSmsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: SmsViewModel
    private lateinit var smsAdapter: SmsAdapter

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val smsPermissionGranted = permissions[Manifest.permission.READ_SMS] == true
        viewModel.setPermissionStatus(smsPermissionGranted)
        updateUI()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(SmsViewModel::class.java)
        
        _binding = FragmentSmsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        
        setupRecyclerView()
        setupObservers()
        checkPermissions()
        setupClickListeners()
        
        return root
    }

    private fun setupRecyclerView() {
        smsAdapter = SmsAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = smsAdapter
        }
    }

    private fun setupObservers() {
        viewModel.filteredMessages.observe(viewLifecycleOwner) { messages ->
            smsAdapter.updateMessages(messages)
            updateEmptyView(messages.isEmpty())
        }

        viewModel.hasPermission.observe(viewLifecycleOwner) { hasPermission ->
            updatePermissionUI(hasPermission)
        }

        viewModel.showBankOnly.observe(viewLifecycleOwner) { showBankOnly ->
            updateFilterUI(showBankOnly)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // You could show a loading indicator here if needed
        }
    }

    private fun setupClickListeners() {
        binding.grantPermissionButton.setOnClickListener {
            requestSmsPermission()
        }
        
        binding.bankFilterSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleBankFilter(isChecked)
        }
    }

    private fun checkPermissions() {
        val hasPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED

        viewModel.setPermissionStatus(hasPermission)
        updateUI()
    }

    private fun requestSmsPermission() {
        requestPermissionLauncher.launch(arrayOf(Manifest.permission.READ_SMS))
    }

    private fun updateUI() {
        val hasPermission = viewModel.hasPermission.value ?: false
        updatePermissionUI(hasPermission)
    }

    private fun updatePermissionUI(hasPermission: Boolean) {
        if (hasPermission) {
            binding.permissionCard.visibility = View.GONE
            binding.filterCard.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.VISIBLE
        } else {
            binding.permissionCard.visibility = View.VISIBLE
            binding.filterCard.visibility = View.GONE
            binding.recyclerView.visibility = View.GONE
            binding.emptyView.visibility = View.GONE
        }
    }

    private fun updateFilterUI(showBankOnly: Boolean) {
        binding.bankFilterSwitch.isChecked = showBankOnly
        binding.filterText.text = if (showBankOnly) {
            getString(R.string.filter_bank_messages)
        } else {
            getString(R.string.filter_all_messages)
        }
    }

    private fun updateEmptyView(isEmpty: Boolean) {
        binding.emptyView.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 