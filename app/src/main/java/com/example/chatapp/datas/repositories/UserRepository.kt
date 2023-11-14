package com.example.chatapp.datas.repositories

import android.net.Uri
import com.example.chatapp.datas.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    fun isExistUser(userId: String): StateFlow<Boolean?>
    fun registerUser(userId: String): StateFlow<Boolean?>
    fun login(userId: String): StateFlow<Boolean?>
    fun logout(userId: String)
    fun getUserBoxId(userId: String): MutableStateFlow<List<String>>
    fun updateUserProfile(userId: String, userName: String): MutableStateFlow<Boolean?>
    fun getUser(userId: String): MutableStateFlow<List<String>>
    fun uploadImage(userId: String, image: Uri): MutableStateFlow<Boolean?>
    fun setUserAvatarUrl(userId: String, image: Uri)

}