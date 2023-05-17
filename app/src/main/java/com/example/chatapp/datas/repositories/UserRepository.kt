package com.example.chatapp.datas.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    fun isExistUser(userId: String): StateFlow<Boolean?>
    fun registerUser(userId: String): StateFlow<Boolean?>
    fun login(userId: String): StateFlow<Boolean?>
    fun logout(userId: String)
}