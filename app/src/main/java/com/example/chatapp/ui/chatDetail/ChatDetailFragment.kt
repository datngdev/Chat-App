package com.example.chatapp.ui.chatDetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
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
        fun navigateToEditBoxChat(boxId: String)
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

        binding.chatTopBackground.setOnClickListener {
            callback!!.navigateToEditBoxChat(boxId)
        }

        binding.chatBtnBack.setOnClickListener {
            callback!!.navigateToHome()
        }

        binding.chatBtnSend.setOnClickListener {
            val newMess = binding.chatData.text.toString()
            if (!newMess.isNullOrEmpty()) {
                chatDetailViewModel.sendMess(currentUser, boxId, newMess)
                binding.chatData.setText("")
            }
        }

        lifecycleScope.launch {
            chatDetailViewModel.getBox(boxId).collect {boxData ->
                if (!boxData.isNullOrEmpty()) {
                    val boxName = boxData[0]
                    val avatarUrl = boxData[1]

                    binding.chatName.text = boxName
                    Glide.with(requireContext())
                        .load(avatarUrl)
                        .centerCrop()
                        .into(binding.chatImageItem)
                }
            }
        }

        val layoutManager = LinearLayoutManager(context)
        layoutManager.reverseLayout = true
        var messList = mutableListOf<Message>()
        val adapter = MessagesAdapter(currentUser, messList, object : MessagesAdapter.MessagesAdapterCallBack {
            override fun loadUserImage(userId: String, img: ImageView) {
                lifecycleScope.launch {
                    chatDetailViewModel.loadUserImage(userId).collect {avatarUrl ->
                        Log.d("avatarUrl", avatarUrl)
                        Glide.with(requireContext())
                            .load(avatarUrl)
                            .centerCrop()
                            .into(img)
                    }
                }
            }
        })

        binding.chatRecyclerView.layoutManager = layoutManager
        binding.chatRecyclerView.adapter = adapter

        lifecycleScope.launch {
            chatDetailViewModel.getMess(currentUser, boxId).collect {
                messList.clear()
                messList.addAll(it.reversed())
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}