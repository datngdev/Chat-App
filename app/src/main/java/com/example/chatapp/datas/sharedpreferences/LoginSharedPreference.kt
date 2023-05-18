package com.example.chatapp.datas.sharedpreferences

import android.content.Context

interface LoginSharedPreference {
    fun getCurrentUserId(): String?
    fun updateLoginStatus(userId: String)
    fun logout()
}