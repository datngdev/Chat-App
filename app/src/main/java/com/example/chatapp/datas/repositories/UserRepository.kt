package com.example.chatapp.datas.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chatapp.datas.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserRepository {
    private val db = Firebase.database
    private val userRef = db.getReference("users")

    fun isExistUser(userId: String): LiveData<Boolean> {
        val isExistUserState = MutableLiveData<Boolean>()

        db.reference.child("users").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                isExistUserState.value = !snapshot.children.none {
                    it.key == userId
                }
            }
            override fun onCancelled(error: DatabaseError) {
                isExistUserState.value = false
            }
        })
        return isExistUserState
    }

    fun registerUser(userId: String): LiveData<Boolean> {
        val registerState = MutableLiveData<Boolean>()

        val defaultAvatar = "defaultAvatar.jpg"
        val newUser = User(userId, defaultAvatar, isActive = false)
        userRef.child(userId).setValue(newUser).addOnSuccessListener {
            registerState.value = true
        }.addOnFailureListener {
            registerState.value = false
        }
        return registerState
    }

    fun login(userId: String): LiveData<Boolean> {
        val loginState = MutableLiveData<Boolean>()

        userRef.child(userId).child("active").setValue("true").addOnSuccessListener {
            loginState.value = true
        }.addOnFailureListener {
            loginState.value = false
        }
        return loginState
    }

    fun logout(userId: String) {
        userRef.child(userId).child("active").setValue("false")
//        userRef.child(userId).child("active").setValue("false").addOnSuccessListener {
//            _logoutState.value = true
//        }.addOnFailureListener {
//            _logoutState.value = false
//        }
    }
}