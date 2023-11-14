package com.example.chatapp.ui.chatDetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.datas.models.Message
import com.example.chatapp.datas.repositories.BoxChatRepository
import com.example.chatapp.datas.repositories.BoxChatRepositoryImpl
import com.example.chatapp.datas.repositories.UserRepository
import com.example.chatapp.datas.repositories.UserRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatDetailViewModel : ViewModel() {
    private val boxRepo: BoxChatRepository = BoxChatRepositoryImpl()
    private val userRepo: UserRepository = UserRepositoryImpl()

    fun getBox(boxId: String): MutableStateFlow<List<String>> {
        val boxInfo = MutableStateFlow(emptyList<String>())
        viewModelScope.launch {
            boxRepo.getBoxInfo(boxId).collect {boxData ->
                if (!boxData.isNullOrEmpty()) {
                    boxInfo.update { boxData }
                }
            }
        }
        return boxInfo
    }

    fun sendMess(userId: String, boxId: String, data: String) {
        boxRepo.sendMess(userId, boxId, data)
    }

    fun getMess(userId: String, boxId: String): MutableStateFlow<List<Message>> {
        val newList = MutableStateFlow(emptyList<Message>())
        viewModelScope.launch {
            boxRepo.getMess(userId, boxId).collect {nList ->
                if (!nList.isNullOrEmpty()) {
                    newList.update { nList }
                }
            }
        }
        return newList
    }

    fun loadUserImage(userId: String) : MutableStateFlow<String> {
        val userInfo = MutableStateFlow("")

        viewModelScope.launch {
            userRepo.getUser(userId).collect {userData ->
                if (!userData.isNullOrEmpty()) {
                    Log.d("ChatApp", userData[1])
                    val avatarUrl = userData[1]
                    userInfo.value = avatarUrl
                }
            }
        }

        return userInfo
    }
}