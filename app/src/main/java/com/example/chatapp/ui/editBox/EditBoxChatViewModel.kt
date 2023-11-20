package com.example.chatapp.ui.editBox

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.datas.models.User
import com.example.chatapp.datas.repositories.BoxRepository
import com.example.chatapp.datas.repositories.BoxRepositoryImpl
import com.example.chatapp.datas.repositories.UserRepository
import com.example.chatapp.datas.repositories.UserRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditBoxChatViewModel : ViewModel() {
    val userRepo: UserRepository = UserRepositoryImpl()
    val boxRepo: BoxRepository = BoxRepositoryImpl()

    fun listenerCurrentUserRemoved(userId: String, boxId: String): MutableStateFlow<Boolean> {
        val resultState = MutableStateFlow(true)
        viewModelScope.launch {
            userRepo.listenerCurrentUserRemoved(userId, boxId).collect {newState ->
                resultState.value = newState
            }
        }
        return resultState
    }

    fun getBoxInfo(boxId: String): MutableStateFlow<List<String>> {
        val resultBoxInfo = MutableStateFlow(emptyList<String>())

        viewModelScope.launch {
            var newBoxInfo = mutableListOf("", "")
            viewModelScope.launch {
                boxRepo.getBoxName(boxId).collect { boxName ->
                    if (!boxName.isNullOrEmpty()) {
                        newBoxInfo[0] = boxName
                        resultBoxInfo.update { newBoxInfo.toList() }
                    }
                }
            }

            boxRepo.getBoxAvatarUrl(boxId).collect {boxAvatarUrl ->
                if (!boxAvatarUrl.isNullOrEmpty()) {
                    newBoxInfo[1] = boxAvatarUrl
                    resultBoxInfo.update { newBoxInfo.toList() }
                }
            }
        }
        return resultBoxInfo
    }

    fun updateBoxAvatar(boxId: String, avatarUri: Uri): MutableStateFlow<Boolean?> {
        val updateState = MutableStateFlow<Boolean?>(null)
        viewModelScope.launch {
            boxRepo.upLoadBoxAvatar(boxId, avatarUri).collect { uploadState ->
                if (uploadState == true) {
                    boxRepo.getBoxAvatarDownloadUrl(boxId).collect {avatarUrl ->
                        if (!avatarUrl.isNullOrEmpty()) {
                            boxRepo.setBoxAvatarUrl(boxId, avatarUrl).collect {setAvatarState ->
                                if (setAvatarState == true) {
                                    updateState.value = true
                                } else if (setAvatarState == false) {
                                    updateState.value = false
                                }
                            }
                        }
                    }
                } else if (uploadState == false) {
                    updateState.value = false
                }
            }
        }
        return  updateState
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
        val resultState = MutableStateFlow<Boolean?>(null)
        viewModelScope.launch {
            userRepo.isExistUser(userId).collect {isExist ->
                if (isExist == true) {
                    userRepo.addToBox(userId, boxId).collect { addState ->
                        if (addState == true) {
                            resultState.value = true
                        } else if (addState == false) {
                            resultState.value = false
                        }
                    }
                } else if (isExist == false) {
                    resultState.value = false
                }
            }
        }
        return resultState
    }

    fun getUserList(boxId: String): MutableStateFlow<List<User>> {
        val resultList = MutableStateFlow(emptyList<User>())

        viewModelScope.launch {
            userRepo.getUserListByBoxId(boxId).collect {userList ->
                if (!userList.isNullOrEmpty()) {
                    resultList.update { userList }
                }
            }
        }

        return resultList
    }

    fun removeUser(userId: String, boxId: String): MutableStateFlow<Boolean?> {
        val resultState = MutableStateFlow<Boolean?>(null)
        viewModelScope.launch {
            userRepo.removeBox(userId, boxId).collect {removeState ->
                if (removeState == true) {
                    boxRepo.removeAdmin(userId, boxId).collect {removeAdminState ->
                        if (removeAdminState == true) {
                            resultState.value = true
                        } else if (removeAdminState == false) {
                            resultState.value = false
                        }
                    }
                } else if (removeState == false) {
                    resultState.value = false
                }
            }
        }
        return resultState
    }

    fun setAdmin(userId: String, boxId: String): MutableStateFlow<Boolean?> {
        val resultState = MutableStateFlow<Boolean?>(null)
        viewModelScope.launch {
            boxRepo.setAdmin(userId, boxId).collect {setAdminState ->
                if (setAdminState == true) {
                    resultState.value = true
                } else if (setAdminState == false) {
                    resultState.value = false
                }
            }
        }
        return resultState
    }

    fun getAdminIdList(boxId: String): MutableStateFlow<List<String>> {
        val resultList = MutableStateFlow(emptyList<String>())
        viewModelScope.launch {
            boxRepo.getAdminIdList(boxId).collect { adminIdList ->
                if (!adminIdList.isNullOrEmpty()) {
                    resultList.update { adminIdList }
                }
            }
        }
        return resultList
    }
}