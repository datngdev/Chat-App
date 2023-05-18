package com.example.chatapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.chatapp.databinding.FragmentHomeBinding
import com.example.chatapp.datas.repositories.UserRepository
import com.example.chatapp.datas.sharedpreferences.LoginSharedPreference
import com.example.chatapp.datas.sharedpreferences.LoginSharedPreferenceImpl

class HomeFragment : Fragment() {
    companion object {
        fun newInstance(callBack: HomeFragmentCallBack): HomeFragment {
            return HomeFragment().apply {
                this.callBack = callBack
            }
        }
    }

    interface HomeFragmentCallBack {
        fun navigateToLogin()
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var callBack: HomeFragmentCallBack? = null
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var loginSharedPreference: LoginSharedPreference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loginSharedPreference = LoginSharedPreferenceImpl(requireContext())
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = loginSharedPreference.getCurrentUserId()!!
        binding.txtHome.text = currentUser

        binding.btnLogout.setOnClickListener {
            homeViewModel.logout(currentUser)
            loginSharedPreference.logout()
            callBack!!.navigateToLogin()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}