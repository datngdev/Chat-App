package com.example.chatapp.datas.sharedpreferences

import android.content.Context

interface LoginSharedPreference {
    fun getCurrentUserId(context: Context): String?
    fun updateLoginStatus(context: Context, userId: String)
    fun logout(context: Context)
}