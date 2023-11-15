package com.example.chatapp.datas.repositories

import android.net.Uri
import com.example.chatapp.datas.models.BoxChat
import com.example.chatapp.datas.models.Message
import com.example.chatapp.datas.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface BoxChatRepository {
    fun createBoxChat(userId: String, boxId: String): StateFlow<Boolean?>
    fun getBoxList(userId: List<String>): MutableStateFlow<List<BoxChat>>
    fun getBoxInfo(boxId: String): MutableStateFlow<List<String>>
    fun sendMess(userId: String, boxId: String, data: String)
    fun getMess(userId: String, boxId: String): MutableStateFlow<List<Message>>
    fun setImage(boxId: String, image: Uri): MutableStateFlow<Boolean?>
    fun setBoxAvatarUrl(userId: String, image: Uri)
    fun setName(boxId: String, name: String): MutableStateFlow<Boolean?>
    fun getUsetList(boxId: String): MutableStateFlow<List<User>>
    fun addUser(boxId: String, userId: String): MutableStateFlow<Boolean?>
}