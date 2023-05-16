package com.example.chatapp.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatapp.datas.repositories.UserRepository

class LoginViewModel : ViewModel() {
    private val userRepo = UserRepository()

    fun isExistUser(userId: String): LiveData<Boolean> {
        return userRepo.isExistUser(userId)
    }

    fun registerUser(userId: String): LiveData<Boolean> {
        return userRepo.registerUser(userId)
    }

    fun login(userId: String): LiveData<Boolean> {
        return userRepo.login(userId)
    }
}