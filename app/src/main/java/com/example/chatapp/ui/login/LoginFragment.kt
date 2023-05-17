package com.example.chatapp.ui.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.chatapp.databinding.FragmentLoginBinding
import com.example.chatapp.datas.sharedpreferences.LoginSharedPreference
import com.example.chatapp.datas.sharedpreferences.LoginSharedPreferenceImpl
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    companion object {
        fun newInstance(callBack: LoginFragmentCallBack): LoginFragment {
            return LoginFragment().apply {
                this.callBack = callBack
            }
        }
    }

    interface LoginFragmentCallBack {
        fun navigateToHome()
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private var callBack: LoginFragmentCallBack? = null
    private val loginViewModel: LoginViewModel by viewModels()
    private val loginSharedPreference: LoginSharedPreference = LoginSharedPreferenceImpl()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val userId = binding.editTextUsername.text.toString()
            Log.d("ChatApp", "UserId: $userId")

            lifecycleScope.launch {
                loginViewModel.processLogin(userId).collect {
                    if(it == true) {
                        loginSharedPreference.updateLoginStatus(requireContext(), userId)
                        callBack!!.navigateToHome()
                    } else if (it == false) {
                        Toast.makeText(context, "Login Fail", Toast.LENGTH_SHORT)
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