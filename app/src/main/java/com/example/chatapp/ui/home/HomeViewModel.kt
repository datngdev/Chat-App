package com.example.chatapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.datas.models.BoxChat
import com.example.chatapp.datas.repositories.BoxRepository
import com.example.chatapp.datas.repositories.BoxRepositoryImpl
import com.example.chatapp.datas.repositories.UserRepository
import com.example.chatapp.datas.repositories.UserRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val userRepo: UserRepository = UserRepositoryImpl()
    private val boxRepo: BoxRepository = BoxRepositoryImpl()

    fun processCreateBoxChat(userId: String, boxName: String): StateFlow<Boolean?> {
        val createBoxStatus = MutableStateFlow<Boolean?>(null)
        val defaultAvatar = "default"
        viewModelScope.launch {
            boxRepo.getBoxAvatarDownloadUrl(defaultAvatar).collect {avatarUrl ->
                if (avatarUrl != null) {
                    if (avatarUrl.isNotEmpty()) {
                        boxRepo.createBoxChat(boxName, avatarUrl).collect {boxId ->
                            if (!boxId.isNullOrEmpty()) {
                                userRepo.addToBox(userId, boxId).collect { addState ->
                                    if (addState == true) {
                                        boxRepo.setAdmin(userId, boxId).collect {setAdminState ->
                                            if (setAdminState == true) {
                                                createBoxStatus.value = true
                                            } else if (setAdminState == false) {
                                                createBoxStatus.value = false
                                            }
                                        }
                                    } else if (addState == false) {
                                        createBoxStatus.value = false
                                    }
                                }
                            } else {
                                createBoxStatus.value = false
                            }
                        }
                    }
                } else {
                    createBoxStatus.value = false
                }
            }
        }
        return createBoxStatus
    }

    fun processGetBoxChat(userId: String): MutableStateFlow<List<BoxChat>> {
        val boxList = MutableStateFlow(emptyList<BoxChat>())

        viewModelScope.launch {
            userRepo.getBoxIdList(userId).collect { boxIdList ->
                if (!boxIdList.isNullOrEmpty()) {
                    viewModelScope.launch {
                        boxRepo.getBoxByIdList(boxIdList).collect { newBoxList ->
                            newBoxList.forEach { boxChat ->
                                if (boxChat.lastMess!!.length > 28) {
                                    boxChat.lastMess = processLongMess(boxChat.lastMess)
                                }
                            }
                            boxList.update { newBoxList }
                        }
                    }
                }
            }
        }

        return boxList
    }

    fun getUnseenCountList(userId: String): MutableStateFlow<List<Map<String, String>>> {
        val resultList = MutableStateFlow(emptyList<Map<String, String>>())
        viewModelScope.launch {
            userRepo.getUserUnseenCountList(userId).collect {unseenList ->
                if (!unseenList.isNullOrEmpty()) {
                    resultList.update { unseenList }
                }
            }
        }
        return resultList
    }

    private fun processLongMess(mess: String): String {
        return mess.take(25).plus("...")
    }
}