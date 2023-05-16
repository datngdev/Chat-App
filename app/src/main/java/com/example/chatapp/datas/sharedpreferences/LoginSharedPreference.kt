package com.example.chatapp.datas.sharedpreferences

import android.content.Context
import android.content.Context.MODE_PRIVATE

class LoginSharedPreference {

    fun getCurrentUserId(context: Context): String? {
        val pref = context.getSharedPreferences("PREF", MODE_PRIVATE)
        return pref.getString("currentUser", "")
    }

    fun updateLoginStatus(context: Context, userId: String) {
        val pref = context.getSharedPreferences("PREF", MODE_PRIVATE)
        pref.edit().putString("currentUser", userId).commit()
    }

    fun logout(context: Context) {
        val pref = context.getSharedPreferences("PREF", MODE_PRIVATE)
        pref.edit().putString("currentUser", "").commit()
    }

}