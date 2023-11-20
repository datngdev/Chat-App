package com.example.chatapp.ui.editProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.chatapp.databinding.EditProfileFragmentBinding
import com.example.chatapp.datas.sharedpreferences.LoginSharedPreference
import com.example.chatapp.datas.sharedpreferences.LoginSharedPreferenceImpl
import kotlinx.coroutines.launch

class EditProfileFragment : Fragment() {
    companion object {
        fun newInstance(callBack: EditProfileCallBack) : EditProfileFragment {
            return EditProfileFragment().apply {
                this.callback = callBack
            }
        }
    }

    interface EditProfileCallBack {
        fun navigateToHome()
        fun navigateToLogin()
    }

    private var _binding: EditProfileFragmentBinding? = null
    private val binding get() = _binding!!

    private var callback: EditProfileCallBack? = null

    private val editProfileViewModel: EditProfileViewModel by viewModels()
    private lateinit var loginSharedPreference: LoginSharedPreference
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loginSharedPreference = LoginSharedPreferenceImpl(requireContext())
        _binding = EditProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUserId = loginSharedPreference.getCurrentUserId()!!

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                lifecycleScope.launch {
                    editProfileViewModel.updateUserAvatar(currentUserId, uri).collect { state ->
                        if (state == true) {
                            binding.editProfileImg.setImageURI(uri)
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

        binding.editProfileImg.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.editProfileUid.text = currentUserId

        binding.editProfileLogout.setOnClickListener {
            editProfileViewModel.logout(currentUserId)
            loginSharedPreference.logout()
            callback!!.navigateToLogin()
        }

        binding.editProfileBtn.setOnClickListener {
            val newUserName = binding.editProfileName.text.toString()
            lifecycleScope.launch {
                editProfileViewModel.updateUserName(currentUserId, newUserName).collect {
                    if (it == true) {
                        Toast.makeText(context, "Update User Profile Successful", Toast.LENGTH_SHORT).show()
                    } else if (it == false) {
                        Toast.makeText(context, "Update User Profile Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.editProfileBtnBack.setOnClickListener {
            callback!!.navigateToHome()
        }

        lifecycleScope.launch {
            editProfileViewModel.getUser(currentUserId).collect { userData ->
                if (!userData.isNullOrEmpty()) {
                    val userName = userData[0]
                    val avatarUrl = userData[1]
                    binding.editProfileName.setText(userName)
                    Glide.with(requireContext())
                        .load(avatarUrl)
                        .centerCrop()
                        .into(binding.editProfileImg)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}