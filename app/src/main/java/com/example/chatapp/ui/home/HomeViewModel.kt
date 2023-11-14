package com.example.chatapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.datas.models.BoxChat
import com.example.chatapp.datas.repositories.BoxChatRepository
import com.example.chatapp.datas.repositories.BoxChatRepositoryImpl
import com.example.chatapp.datas.repositories.UserRepository
import com.example.chatapp.datas.repositories.UserRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val boxChatRepo: BoxChatRepository = BoxChatRepositoryImpl()

    private val userRepo: UserRepository = UserRepositoryImpl()
    private val boxRepo: BoxChatRepository = BoxChatRepositoryImpl()

    fun logout(userId: String) {
        userRepo.logout(userId)
    }

    fun processCreateBoxChat(userId: String, boxId: String): StateFlow<Boolean?> {
        val createBoxStatus = MutableStateFlow<Boolean?>(null)

        viewModelScope.launch {
            boxChatRepo.createBoxChat(userId, boxId).collect {
                if (it == true) {
                    createBoxStatus.value = true
                } else if (it == false) {
                    createBoxStatus.value = false
                }
            }
        }

        return createBoxStatus
    }

    fun processGetBoxChat(userId: String): MutableStateFlow<List<BoxChat>> {
        val boxList = MutableStateFlow(emptyList<BoxChat>())

        viewModelScope.launch {
            userRepo.getUserBoxId(userId).collect { boxId ->
                if (!boxId.isNullOrEmpty()) {
                    viewModelScope.launch {
                        boxRepo.getBoxList(boxId).collect { newBoxList ->
                            boxList.update { newBoxList }
                        }
                    }
                }
            }
        }

        return boxList
    }

}