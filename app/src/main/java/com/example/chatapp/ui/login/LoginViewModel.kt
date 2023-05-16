package com.example.chatapp.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatapp.datas.repositories.UserRepository
import com.example.chatapp.datas.repositories.UserRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val userRepo : UserRepository = UserRepositoryImpl()

    val viewModelScope = CoroutineScope(Dispatchers.Default)

    fun isExistUser(userId: String){
        viewModelScope.launch {
            userRepo.isExistUser(userId).collect{

            }
        }
    }

    fun registerUser(userId: String): LiveData<Boolean> {
        return userRepo.registerUser(userId)
    }

    fun login(userId: String): LiveData<Boolean> {
        return userRepo.login(userId)
    }
}