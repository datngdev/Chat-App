package com.example.chatapp.datas.repositories

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    fun isExistUser(userId: String): StateFlow<Boolean?>
    fun registerUser(userId: String): StateFlow<Boolean?>
    fun login(userId: String): StateFlow<Boolean?>
    fun logout(userId: String)
    fun getBoxId(userId: String): MutableStateFlow<List<String>>
}