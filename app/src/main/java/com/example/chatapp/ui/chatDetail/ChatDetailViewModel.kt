package com.example.chatapp.ui.chatDetail

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.datas.models.Message
import com.example.chatapp.datas.repositories.BoxRepository
import com.example.chatapp.datas.repositories.BoxRepositoryImpl
import com.example.chatapp.datas.repositories.UserRepository
import com.example.chatapp.datas.repositories.UserRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ChatDetailViewModel : ViewModel() {
    private val boxRepo: BoxRepository = BoxRepositoryImpl()
    private val userRepo: UserRepository = UserRepositoryImpl()

    fun listenerCurrentUserRemoved(userId: String, boxId: String): MutableStateFlow<Boolean> {
        val resultState = MutableStateFlow(true)
        viewModelScope.launch {
            userRepo.listenerCurrentUserRemoved(userId, boxId).collect {newState ->
                resultState.value = newState
            }
        }
        return resultState
    }

    fun getBox(boxId: String): MutableStateFlow<List<String>> {
        val resultBoxInfo = MutableStateFlow(emptyList<String>())
        viewModelScope.launch {
            var newBoxInfo = mutableListOf("", "")
            viewModelScope.launch {
                boxRepo.getBoxName(boxId).collect {boxName ->
                    if (!boxName.isNullOrEmpty()) {
                        newBoxInfo[0] = boxName
                        resultBoxInfo.update { newBoxInfo.toList() }
                    }
                }
            }
            boxRepo.getBoxAvatarUrl(boxId).collect {avatarUrl ->
                if (!avatarUrl.isNullOrEmpty()) {
                    newBoxInfo[1] = avatarUrl
                    resultBoxInfo.update { newBoxInfo.toList() }
                }
            }
        }
        return resultBoxInfo
    }

    fun sendMess(userId: String, boxId: String, data: String, type: Int) {
        boxRepo.sendMess(userId, boxId, data, type)
        viewModelScope.launch {
            userRepo.getUserListByBoxId(boxId).collect {userIdList ->
                userIdList.forEach { user ->
                    if (user.id != userId) {
                        userRepo.setUserUnseenCount(user.id, boxId, 1)
                    }
                }
            }
        }
    }

    fun getMess(userId: String, boxId: String): MutableStateFlow<List<Message>> {
        val resultList = MutableStateFlow(emptyList<Message>())
        viewModelScope.launch {
            boxRepo.getMessList(userId, boxId).collect { nList ->
                if (!nList.isNullOrEmpty()) {
                    var newMessList = mutableListOf(nList)
                    newMessList.forEach {
                        resultList.update { nList }
                    }
                }
            }
        }
        return resultList
    }

    fun getUserAvatarUrl(userId: String) : MutableStateFlow<String> {
        val resultAvatarUrl = MutableStateFlow("")

        viewModelScope.launch {
            userRepo.getUserAvatarUrl(userId).collect {avatarUrl ->
                if (!avatarUrl.isNullOrEmpty()) {
                    resultAvatarUrl.value = avatarUrl
                }
            }
        }

        return resultAvatarUrl
    }

    fun uploadImage(boxId: String, imageUri: Uri): MutableStateFlow<String?> {
        val imageUrl = MutableStateFlow<String?>(null)
        viewModelScope.launch {
            boxRepo.uploadImage(boxId, imageUri).collect {newImageUrl ->
                if (newImageUrl != null) {
                    imageUrl.update { newImageUrl }
                }
            }
        }
        return imageUrl
    }

    fun getBoxOnlineState(userId: String, boxId: String): MutableStateFlow<Boolean?> {
        val resultState = MutableStateFlow<Boolean?>(null)
        viewModelScope.launch {
            userRepo.getBoxOnlineState(userId, boxId).collect {newState ->
                resultState.update { newState }
            }
        }
        return resultState
    }

    fun resetUnseenCount(userId: String, boxId: String) {
        viewModelScope.launch {
            userRepo.resetUserUnseenCount(userId, boxId)
        }
    }

    fun removeUnseenListener(userId: String, boxId: String) {
        userRepo.removeUnseenCountListener(userId, boxId)
    }
}