package com.example.chatapp.datas.repositories

import com.example.chatapp.datas.models.BoxChat
import com.example.chatapp.datas.models.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface BoxChatRepository {
    fun createBoxChat(userId: String, boxId: String): StateFlow<Boolean?>
    fun getBoxList(userId: List<String>): MutableStateFlow<List<BoxChat>>
    fun getBoxInfo(boxId: String): MutableStateFlow<List<String>>
    fun sendMess(userId: String, boxId: String, data: String)
    fun getMess(userId: String, boxId: String): MutableStateFlow<List<Message>>
}