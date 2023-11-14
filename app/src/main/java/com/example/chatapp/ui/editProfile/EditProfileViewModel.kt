package com.example.chatapp.ui.editProfile

import android.net.Uri
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
            userRepo.updateUserProfile(userId, userName).collect {
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
        val userInfo = MutableStateFlow(emptyList<String>())

        viewModelScope.launch {
            userRepo.getUser(userId).collect() {userData ->
                if (!userData.isNullOrEmpty()) {
                    userInfo.update { userData }
                }
            }
        }
        return userInfo
    }

    fun updateUserAvatar(userId: String, image: Uri): MutableStateFlow<Boolean?> {
        val uploadState = MutableStateFlow<Boolean?>(null)
        viewModelScope.launch {
            userRepo.uploadImage(userId, image).collect {state ->
                if (state == true) {
                    uploadState.value = true
                } else if (state == false) {
                    uploadState.value = false
                }
            }
        }
        return  uploadState
    }
}