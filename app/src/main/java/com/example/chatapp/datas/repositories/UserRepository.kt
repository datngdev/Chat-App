package com.example.chatapp.datas.repositories

import android.net.Uri
import com.example.chatapp.datas.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    fun isExistUser(userId: String): StateFlow<Boolean?>
    fun registerUser(userId: String, avatarUrl: String): StateFlow<Boolean?>
    fun login(userId: String): StateFlow<Boolean?>
    fun logout(userId: String)
    fun getUserName(userId: String): MutableStateFlow<String?>
    fun getUserAvatarUrl(userId: String): MutableStateFlow<String>
    fun setUserName(userId: String, userName: String): MutableStateFlow<Boolean?>
    fun uploadUserAvatar(userId: String, avatarUri: Uri): MutableStateFlow<Boolean?>
    fun getUserAvatarDownloadUrl(userId: String): MutableStateFlow<String?>
    fun setUserAvatarUrl(userId: String, avatarUrl: String): MutableStateFlow<Boolean?>
    fun addToBox(userId: String, boxId: String): MutableStateFlow<Boolean?>
    fun getBoxIdList(userId: String): MutableStateFlow<List<String>>
    fun getUserListByBoxId(boxId: String): MutableStateFlow<List<User>>
    fun removeBox(userId: String, boxId: String): MutableStateFlow<Boolean?>
    fun listenerCurrentUserRemoved(userId: String, boxId: String): MutableStateFlow<Boolean>
    fun getBoxOnlineState(currentUserId: String, boxId: String): MutableStateFlow<Boolean?>
    fun setUserUnseenCount(userId: String, boxId: String, number: Int)
    fun resetUserUnseenCount(userId: String, boxId: String)
    fun removeUnseenCountListener(userId: String, boxId: String)
    fun getUserUnseenCountList(userId: String): MutableStateFlow<List<Map<String, String>>>
}