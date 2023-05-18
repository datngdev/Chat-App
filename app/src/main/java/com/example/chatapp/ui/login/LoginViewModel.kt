package com.example.chatapp.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.datas.repositories.UserRepository
import com.example.chatapp.datas.repositories.UserRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val userRepo: UserRepository = UserRepositoryImpl()

    private val loginState = MutableStateFlow<Boolean?>(null)
    fun processLogin(userId: String): StateFlow<Boolean?> {
        viewModelScope.launch {
            userRepo.isExistUser(userId).collect {
                if(it == true) {
                    login(userId)
                } else if (it == false) {
                    registerAndLogin(userId)
                }
            }
        }
        return loginState

    }

    private suspend fun registerAndLogin(userId: String) {
        userRepo.registerUser(userId).collect() {
            if(it == true) {
                login(userId)
            } else if (it == false) {
                Log.d("ChatApp", "Register Fail")
                loginState.value = false
            }
        }
    }

    private suspend fun login(userId: String) {
        userRepo.login(userId).collect {
            if (it == true) {
                loginState.value = true
            } else if (it == false) {
                Log.d("ChatApp", "Login Fail")
                loginState.value = false
            }
        }
    }
}