package com.example.chatapp.ui.editProfile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.datas.repositories.UserRepository
import com.example.chatapp.datas.repositories.UserRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditProfileViewModel : ViewModel() {
    private val userRepo: UserRepository = UserRepositoryImpl()

    fun logout(userId: String) {
        userRepo.logout(userId)
    }

    fun updateUserName(userId: String, userName: String) : MutableStateFlow<Boolean?> {
        val updateState = MutableStateFlow<Boolean?>(null)

        viewModelScope.launch {
            userRepo.setUserName(userId, userName).collect {
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

    fun getUser(userId: String): MutableStateFlow<List<String>> {
        val resultUserData = MutableStateFlow(emptyList<String>())


        viewModelScope.launch {
            var userData = mutableListOf("","")
            viewModelScope.launch {
                userRepo.getUserName(userId).collect { userName ->
                    if (!userName.isNullOrEmpty()) {
                        userData[0] = userName
                        resultUserData.update { userData.toList() }
                    }
                }
            }
            userRepo.getUserAvatarUrl(userId).collect {avatarUrl ->
                if (!avatarUrl.isNullOrEmpty()) {
                    userData[1] = avatarUrl
                    resultUserData.update { userData.toList() }
                }
            }
        }



        return resultUserData
    }

    fun updateUserAvatar(userId: String, image: Uri): MutableStateFlow<Boolean?> {
        val updateState = MutableStateFlow<Boolean?>(null)
        viewModelScope.launch {
            userRepo.uploadUserAvatar(userId, image).collect {uploadState ->
                if (uploadState == true) {
                    userRepo.getUserAvatarDownloadUrl(userId).collect {avatarUrl ->
                        if(!avatarUrl.isNullOrEmpty()) {
                            userRepo.setUserAvatarUrl(userId, avatarUrl).collect {setAvatarState ->
                                if (setAvatarState == true) {
                                    updateState.value = true
                                }
                                else if (setAvatarState == false) {
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
}