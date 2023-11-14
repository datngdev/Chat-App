package com.example.chatapp.datas.repositories

import android.net.Uri
import android.util.Log
import com.example.chatapp.datas.models.User
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update

class UserRepositoryImpl : UserRepository {
    private val db = Firebase.database
    private val storage = Firebase.storage

    private val userRef = db.getReference("users")
    private val boxRef = db.getReference("boxChats")
    private val avatarRef = storage.getReference("UserAvatar")

    override fun isExistUser(userId: String): StateFlow<Boolean?> {
        val isExistUserState = MutableStateFlow<Boolean?>(null)

        db.reference.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
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
        avatarRef.child("default.jpg").downloadUrl.addOnSuccessListener {
            val avatarUrl = it.toString()
            var boxIdList = mutableListOf<String>()
            val newUser = User(userId, avatarUrl, isActive = false, boxIdList)
            userRef.child(userId).setValue(newUser).addOnSuccessListener {
                registerState.value = true
            }.addOnFailureListener {
                registerState.value = false
            }
        }.addOnFailureListener {
            registerState.value = false
        }.addOnCanceledListener {
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

    override fun getUserBoxId(userId: String): MutableStateFlow<List<String>> {
        val boxId = MutableStateFlow(emptyList<String>())

        userRef.child(userId).child("boxIdList").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var newBoxId = mutableListOf<String>()
                snapshot.children.forEach {
                    newBoxId.add(it.key.toString())
                }
//                Log.d("Chat", newBoxId.toString())
                boxId.update { newBoxId }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ChatApp", "getBoxId Cancelled")
            }

        })
        return boxId
    }

    override fun updateUserProfile(userId: String, userName: String): MutableStateFlow<Boolean?> {
        val updateState = MutableStateFlow<Boolean?>(null)

        userRef.child(userId).child("name").setValue(userName).addOnSuccessListener {
            updateState.value = true
        }.addOnFailureListener {
            updateState.value = false
        }.addOnCanceledListener {
            updateState.value = false
        }
        return updateState
    }

    override fun getUser(userId: String): MutableStateFlow<List<String>> {
        val userInfo = MutableStateFlow(emptyList<String>())

        userRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newList = mutableListOf<String>()
                val userName = snapshot.child("name").value.toString()
                val userAvatar = snapshot.child("avatar").value.toString()
                newList.add(userName)
                newList.add(userAvatar)

                userInfo.update { newList }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ChatApp", "getUser Cancelled")
            }
        })
        return userInfo
    }

    override fun uploadImage(userId: String, image: Uri): MutableStateFlow<Boolean?> {
        val uploadState = MutableStateFlow<Boolean?>(null)
        val userAvatar = avatarRef.child("$userId.jpg")
        userAvatar.putFile(image).addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener { uri ->
                setUserAvatarUrl(userId, uri)
                uploadState.value = true
            }
        }.addOnFailureListener {
            uploadState.value = false
        }.addOnCanceledListener {
            uploadState.value = false
        }

        return uploadState
    }

    override fun setUserAvatarUrl(userId: String, image: Uri) {
        userRef.child(userId).child("avatar").setValue(image.toString())
    }
}