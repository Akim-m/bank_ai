package com.example.bank_ai.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bank_ai.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: HomeViewModel
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        
        return root
    }
    
    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter()
        binding.chatRecyclerView.apply {
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = true
            }
            adapter = chatAdapter
        }
    }
    
    private fun setupObservers() {
        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            chatAdapter.updateMessages(messages)
            binding.chatRecyclerView.post {
                binding.chatRecyclerView.smoothScrollToPosition(messages.size - 1)
            }
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.sendButton.isEnabled = !isLoading
            binding.messageInput.isEnabled = !isLoading
            if (isLoading) {
                binding.statusText.text = "AI is thinking..."
            } else {
                binding.statusText.text = "Online"
            }
        }
        
        viewModel.modelLoaded.observe(viewLifecycleOwner) { loaded ->
            if (!loaded) {
                binding.messageInput.hint = "Model not loaded. Please try again."
                binding.statusText.text = "Offline"
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.sendButton.setOnClickListener {
            sendMessage()
        }
        
        binding.clearChatButton.setOnClickListener {
            viewModel.clearChat()
            Toast.makeText(context, "Chat cleared", Toast.LENGTH_SHORT).show()
        }
        
        binding.messageInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                true
            } else {
                false
            }
        }
    }
    
    private fun sendMessage() {
        val message = binding.messageInput.text.toString().trim()
        if (message.isNotEmpty()) {
            viewModel.sendMessage(message)
            binding.messageInput.text?.clear()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}