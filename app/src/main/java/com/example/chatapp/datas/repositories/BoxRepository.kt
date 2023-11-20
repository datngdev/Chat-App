package com.example.chatapp.datas.repositories

import android.net.Uri
import com.example.chatapp.datas.models.BoxChat
import com.example.chatapp.datas.models.Message
import com.example.chatapp.datas.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface BoxRepository {
    fun createBoxChat(boxName: String, boxAvatarUrl: String): MutableStateFlow<String?>
    fun upLoadBoxAvatar(boxId: String, image: Uri): MutableStateFlow<Boolean?>
    fun getBoxAvatarDownloadUrl(boxId: String): MutableStateFlow<String?>
    fun setName(boxId: String, name: String): MutableStateFlow<Boolean?>
    fun setBoxAvatarUrl(boxId: String, avatarUrl: String):  MutableStateFlow<Boolean?>
    fun getBoxName(boxId: String): MutableStateFlow<String>
    fun getBoxAvatarUrl(boxId: String): MutableStateFlow<String?>
    fun getBoxByIdList(boxIdList: List<String>): MutableStateFlow<List<BoxChat>>
    fun sendMess(userId: String, boxId: String, data: String, type: Int): MutableStateFlow<Boolean?>
    fun getMessList(userId: String, boxId: String): MutableStateFlow<List<Message>>
    fun setAdmin(userId: String, boxId: String): MutableStateFlow<Boolean?>
    fun removeAdmin(userId: String, boxId: String): MutableStateFlow<Boolean?>
    fun getAdminIdList(boxId: String): MutableStateFlow<List<String>>
    fun uploadImage(boxId: String, image: Uri): MutableStateFlow<String?>
}