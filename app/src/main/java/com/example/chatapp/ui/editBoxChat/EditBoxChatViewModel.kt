package com.example.chatapp.ui.editBoxChat

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.datas.models.BoxChat
import com.example.chatapp.datas.repositories.BoxChatRepository
import com.example.chatapp.datas.repositories.BoxChatRepositoryImpl
import com.example.chatapp.datas.repositories.UserRepository
import com.example.chatapp.datas.repositories.UserRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditBoxChatViewModel : ViewModel() {
    val userRepo: UserRepository = UserRepositoryImpl()
    val boxRepo: BoxChatRepository = BoxChatRepositoryImpl()

    fun getBoxInfo(boxId: String): MutableStateFlow<List<String>> {
        val boxInfo = MutableStateFlow(emptyList<String>())
        viewModelScope.launch {
            boxRepo.getBoxInfo(boxId).collect { boxData ->
                if (!boxData.isNullOrEmpty()) {
                    boxInfo.update { boxData }
                }
            }
        }
        return boxInfo
    }

    fun updateBoxAvatar(boxId: String, image: Uri): MutableStateFlow<Boolean?> {
        val uploadState = MutableStateFlow<Boolean?>(null)
        viewModelScope.launch {
            boxRepo.setImage(boxId, image).collect {state ->
                if (state == true) {
                    uploadState.value = true
                } else if (state == false) {
                    uploadState.value = false
                }
            }
        }
        return  uploadState
    }

    fun setName(boxId: String, name: String): MutableStateFlow<Boolean?> {
        val updateState = MutableStateFlow<Boolean?>(null)

        viewModelScope.launch {
            boxRepo.setName(boxId, name).collect {
                if (it == true) {
                    updateState.value = true
                }
                else if (it == false) {
                    updateState.value = false
                }
            }
        }

        return updateState
    }

    fun addUser(boxId: String, userId: String): MutableStateFlow<Boolean?> {
        val addState = MutableStateFlow<Boolean?>(null)
        viewModelScope.launch {
            boxRepo.addUser(boxId, userId).collect {state ->
                if (state == true) {
                    addState.value = true
                } else if (state == false) {
                    addState.value = false
                }
            }
        }
        return addState
    }

}