package com.example.chatapp.ui.chatDetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatapp.databinding.FragmentChatDetailBinding
import com.example.chatapp.datas.models.Message
import com.example.chatapp.datas.sharedpreferences.LoginSharedPreference
import com.example.chatapp.datas.sharedpreferences.LoginSharedPreferenceImpl
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChatDetailFragment : Fragment() {
    companion object {
        fun newInstance(callBack: ChatDetailCallBack, boxId: String): ChatDetailFragment {
            return ChatDetailFragment().apply {
                this.callBack = callBack
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

    private var callBack: ChatDetailFragment.ChatDetailCallBack? = null
    private lateinit var boxId: String
    private lateinit var currentUser: String

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

        currentUser = sharedPreference.getCurrentUserId()!!

        lifecycleScope.launch {
            chatDetailViewModel.listenerCurrentUserRemoved(currentUser, boxId).collect {newState ->
                if (!newState) {
                    callBack!!.navigateToHome()
                }
            }
        }

        lifecycleScope.launch {
            chatDetailViewModel.resetUnseenCount(currentUser, boxId)
        }

        lifecycleScope.launch {
            chatDetailViewModel.getBoxOnlineState(currentUser, boxId).collect {newState ->
                if (newState == true) {
                    binding.chatActiveState.visibility = View.VISIBLE
                } else if (newState == false) {
                    binding.chatActiveState.visibility = View.INVISIBLE
                }
            }
        }

        binding.chatTopBackground.setOnClickListener {
            callBack!!.navigateToEditBoxChat(boxId)
        }

        binding.chatBtnBack.setOnClickListener {
            callBack!!.navigateToHome()
        }

        binding.chatBtnSend.setOnClickListener {
            val newMess = binding.chatData.text.toString()
            if (!newMess.isNullOrEmpty()) {
                chatDetailViewModel.sendMess(currentUser, boxId, newMess, type = 1)
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
                    chatDetailViewModel.getUserAvatarUrl(userId).collect { avatarUrl ->
                        Glide.with(requireContext())
                            .load(avatarUrl)
                            .centerCrop()
                            .into(img)
                    }
                }
            }

            override fun loadDataImage(imageUrl: String, imageView: ImageView) {
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(requireContext())
                        .load(imageUrl)
                        .into(imageView)
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

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { imageUri ->
            if (imageUri != null) {
                lifecycleScope.launch {
                    chatDetailViewModel.uploadImage(boxId, imageUri).collect {imageUrl ->
                        if (imageUrl != null) {
                            chatDetailViewModel.sendMess(currentUser, boxId, imageUrl, type = 2)
                        }
                    }
                }
            } else {
                Toast.makeText(context, "No Media Selected", Toast.LENGTH_SHORT).show()
            }
        }

        binding.chatBtnAttach.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    override fun onPause() {
        super.onPause()
        chatDetailViewModel.removeUnseenListener(currentUser, boxId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}