package com.example.chatapp.datas.repositories

import com.example.chatapp.datas.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserRepositoryImpl : UserRepository {
    private val db = Firebase.database
    private val userRef = db.getReference("users")

    override fun isExistUser(userId: String): StateFlow<Boolean?> {
        val isExistUserState = MutableStateFlow(false)

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

    override fun registerUser(userId: String): StateFlow<Boolean?> {
        val registerState = MutableStateFlow<Boolean?>(null)

        val defaultAvatar = "defaultAvatar.jpg"
        val newUser = User(userId, defaultAvatar, isActive = false)
        userRef.child(userId).setValue(newUser).addOnSuccessListener {
            registerState.value = true
        }.addOnFailureListener {
            registerState.value = false
        }
        return registerState
    }

    override fun login(userId: String): StateFlow<Boolean?> {
        val loginState = MutableStateFlow<Boolean?>(null)

        userRef.child(userId).child("active").setValue("true").addOnSuccessListener {
            loginState.value = true
        }.addOnFailureListener {
            loginState.value = false
        }
        return loginState
    }

    override fun logout(userId: String) {
        userRef.child(userId).child("active").setValue("false")
    }
}