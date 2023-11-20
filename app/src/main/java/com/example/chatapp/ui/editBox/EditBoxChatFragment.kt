package com.example.chatapp.ui.editBox

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
import com.example.chatapp.databinding.FragmentEditBoxChatBinding
import com.example.chatapp.datas.models.User
import com.example.chatapp.datas.sharedpreferences.LoginSharedPreference
import com.example.chatapp.datas.sharedpreferences.LoginSharedPreferenceImpl
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext

class EditBoxChatFragment : Fragment() {
    companion object {
        fun newInstance(boxId: String, callBack: EditBoxChatCallBack): EditBoxChatFragment {
            return EditBoxChatFragment().apply {
                this.callBack = callBack
                this.boxId = boxId
            }
        }
    }

    interface EditBoxChatCallBack {
        fun navigateToBoxDetail()
        fun navigateToHome()
    }

    private var _binding: FragmentEditBoxChatBinding? = null
    private val binding get() = _binding!!
    private val editBoxChatViewModel: EditBoxChatViewModel by viewModels()
    private lateinit var callBack: EditBoxChatCallBack
    private lateinit var boxId: String
    private lateinit var loginSharedPreference: LoginSharedPreference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loginSharedPreference = LoginSharedPreferenceImpl(requireContext())
        _binding = FragmentEditBoxChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUserId = loginSharedPreference.getCurrentUserId()!!

        lifecycleScope.launch {
            editBoxChatViewModel.listenerCurrentUserRemoved(currentUserId, boxId).collect {newState ->
                if (!newState) {
                    callBack.navigateToHome()
                }
            }
        }

        binding.editBoxBtnBack.setOnClickListener {
            callBack.navigateToBoxDetail()
        }

        lifecycleScope.launch {
            editBoxChatViewModel.getBoxInfo(boxId).collect {
                if (!it.isNullOrEmpty()) {
                    val boxName = it[0]
                    val boxAvatar = it[1]

                    binding.editBoxName.setText(boxName)
                    Glide.with(requireContext())
                        .load(boxAvatar)
                        .centerCrop()
                        .into(binding.editBoxImage)
                }
            }
        }

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                lifecycleScope.launch {
                    editBoxChatViewModel.updateBoxAvatar(boxId, uri).collect {state ->
                        if (state == true) {
                            binding.editBoxImage.setImageURI(uri)
                            Toast.makeText(context, "Avatar Changed", Toast.LENGTH_SHORT).show()
                        } else if (state == false) {
                            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(context, "No Media Selected", Toast.LENGTH_SHORT).show()
            }
        }

        binding.editBoxImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.editBoxBtnUpdate.setOnClickListener {
            val newUserName = binding.editBoxName.text.toString()
            if (!newUserName.isNullOrEmpty()) {
                lifecycleScope.launch {
                    editBoxChatViewModel.setName(boxId, newUserName).collect {
                        if (it == true) {
                            Toast.makeText(context, "Update Box Name Successful", Toast.LENGTH_SHORT).show()
                        } else if (it == false) {
                            Toast.makeText(context, "Update Box Name Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(context, "Box Name Can Not Empty", Toast.LENGTH_SHORT).show()
            }
        }

        binding.editBoxBtnAddUser.setOnClickListener {
            val userId = binding.editBoxAddUser.text.toString()

            if (!userId.isNullOrEmpty()) {
                lifecycleScope.launch {
                    editBoxChatViewModel.addUser(boxId, userId).collect {state ->
                        if (state == true) {
                            Toast.makeText(context, "add user successful", Toast.LENGTH_SHORT).show()
                        } else if (state == false) {
                            Toast.makeText(context, "add user failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        val adminIdList = mutableListOf<String>()
        val userList = mutableListOf<User>()
        val layoutManager = LinearLayoutManager(context)

        val adapter = UserListAdapter(currentUserId, userList, adminIdList, object: UserListAdapter.UserListAdapterCallBack {
            override fun loadUserAvatar(avatarImv: ImageView, avatarUrl: String) {
                if (!avatarUrl.isNullOrEmpty()) {
                    Glide.with(requireContext())
                        .load(avatarUrl)
                        .centerCrop()
                        .into(avatarImv)
                }
            }

            override fun onClickBtnRemove(userId: String) {
                lifecycleScope.launch {
                    editBoxChatViewModel.removeUser(userId, boxId).collect {removeState ->
                        if (removeState == true) {
                            Toast.makeText(context,"Remove User Successful", Toast.LENGTH_SHORT).show()
                        } else if (removeState == false) {
                            Toast.makeText(context,"Remove User Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onclickBtnSetAdmin(userId: String) {
                editBoxChatViewModel.setAdmin(userId, boxId)
            }

        })

        binding.editBoxRecyclerView.layoutManager = layoutManager
        binding.editBoxRecyclerView.adapter = adapter

        lifecycleScope.launch {
            editBoxChatViewModel.getAdminIdList(boxId).collect {newIdList ->
                adminIdList.clear()
                adminIdList.addAll(newIdList)
                adapter.notifyDataSetChanged()
                lifecycleScope.launch {
                    editBoxChatViewModel.getUserList(boxId).collect {newUserList ->
                        userList.clear()
                        userList.addAll(newUserList)
                        adapter.notifyDataSetChanged()
                    }
                }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}