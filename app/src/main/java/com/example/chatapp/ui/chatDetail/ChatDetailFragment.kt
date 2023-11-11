package com.example.chatapp.ui.chatDetail

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.databinding.FragmentChatDetailBinding
import com.example.chatapp.datas.models.Message
import com.example.chatapp.datas.sharedpreferences.LoginSharedPreference
import com.example.chatapp.datas.sharedpreferences.LoginSharedPreferenceImpl
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChatDetailFragment : Fragment() {
    companion object {
        fun newInstance(callBack: ChatDetailCallBack, boxId: String): ChatDetailFragment {
            return ChatDetailFragment().apply {
                this.callback = callBack
                this.boxId = boxId
            }
        }
    }

    interface ChatDetailCallBack {
        fun navigateToHome()
    }

    private var _binding: FragmentChatDetailBinding? = null
    private val binding get() = _binding!!

    private val chatDetailViewModel: ChatDetailViewModel by viewModels()

    private var callback: ChatDetailFragment.ChatDetailCallBack? = null
    private lateinit var boxId: String

    private lateinit var sharedPreference: LoginSharedPreference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreference = LoginSharedPreferenceImpl(requireContext())
        _binding = FragmentChatDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = sharedPreference.getCurrentUserId()!!

        binding.chatBtnSend.setOnClickListener {
            val newMess = binding.chatData.text.toString()
            if (!newMess.isNullOrEmpty()) {
                chatDetailViewModel.sendMess(currentUser, boxId, newMess)
                binding.chatData.setText("")
            }
        }

        val layoutmanager = LinearLayoutManager(context)
        var messList = mutableListOf<Message>()
        val adapter = MessagesAdapter(messList)

        binding.chatRecyclerView.layoutManager = layoutmanager
        binding.chatRecyclerView.adapter = adapter

        lifecycleScope.launch {
            chatDetailViewModel.getMess(currentUser, boxId).collect {
                messList.clear()
                messList.addAll(it)

                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}