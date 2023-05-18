package com.example.chatapp.datas.sharedpreferences

import android.content.Context
import android.content.Context.MODE_PRIVATE

class LoginSharedPreferenceImpl(context: Context) : LoginSharedPreference {
    private val pref = context.getSharedPreferences("PREF", MODE_PRIVATE)
    override fun getCurrentUserId(): String? {
        return pref.getString("currentUser", "")
    }

    override fun updateLoginStatus(userId: String) {
        pref.edit().putString("currentUser", userId).commit()
    }

    override fun logout() {
        pref.edit().putString("currentUser", "").commit()
    }
}