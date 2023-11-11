package com.example.chatapp.ui.chatDetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.datas.models.Message
import com.example.chatapp.datas.repositories.BoxChatRepository
import com.example.chatapp.datas.repositories.BoxChatRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatDetailViewModel : ViewModel() {
    val boxRepo: BoxChatRepository = BoxChatRepositoryImpl()

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
                Log.d("messList vm", newList.value.toString())
            }
        }
        return newList
    }
}