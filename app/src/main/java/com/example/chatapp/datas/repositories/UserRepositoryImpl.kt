package com.example.chatapp.datas.repositories

import android.net.Uri
import android.util.Log
import com.example.chatapp.datas.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.values
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class UserRepositoryImpl : UserRepository {
    private val db = Firebase.database
    private val storage = Firebase.storage

    private val userRef = db.getReference("users")
    private val userAvatarRef = storage.getReference("UserAvatar")

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

    override fun registerUser(userId: String, avatarUrl: String): StateFlow<Boolean?> {
        val registerState = MutableStateFlow<Boolean?>(null)

        var boxIdList = mutableListOf<String>()
        val newUser = User(userId, name = userId, avatarUrl, isActive = false, boxIdList)

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

    override fun getUserName(userId: String): MutableStateFlow<String?> {
        val userName = MutableStateFlow<String?>(null)

        userRef.child(userId).child("name").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userName.value = snapshot.value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Chat App", "getUserName Cancelled")
            }
        })

        return userName
    }

    override fun getUserAvatarUrl(userId: String): MutableStateFlow<String> {
        val avatarUrl = MutableStateFlow("")
        userRef.child(userId).child("avatar").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                avatarUrl.value = snapshot.value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ChatApp", "getUserAvatarUrl Cancelled")
            }

        })

        return avatarUrl
    }

    override fun uploadUserAvatar(userId: String, avatarUri: Uri): MutableStateFlow<Boolean?> {
        val uploadState = MutableStateFlow<Boolean?>(null)
        val userAvatar = userAvatarRef.child("$userId.jpg")

        userAvatar.putFile(avatarUri).addOnSuccessListener {
            uploadState.value = true
        }.addOnFailureListener {
            uploadState.value = false
        }.addOnCanceledListener {
            uploadState.value = false
        }

        return uploadState
    }

    override fun getUserAvatarDownloadUrl(userId: String): MutableStateFlow<String?> {
        val resultUrl = MutableStateFlow<String?>(null)

        userAvatarRef.child("$userId.jpg").downloadUrl.addOnSuccessListener { avatarUri ->
            resultUrl.value = avatarUri.toString()
        }.addOnFailureListener {
            resultUrl.value = ""
        }

        return resultUrl
    }

    override fun setUserAvatarUrl(userId: String, avatarUrl: String): MutableStateFlow<Boolean?> {
        val updateState = MutableStateFlow<Boolean?>(null)

        userRef.child(userId).child("avatar").setValue(avatarUrl).addOnSuccessListener {
            updateState.value = true
        }.addOnFailureListener {
            updateState.value = false
        }.addOnCanceledListener {
            updateState.value = false
        }

        return updateState
    }

    override fun setUserName(userId: String, newName: String): MutableStateFlow<Boolean?> {
        val updateState = MutableStateFlow<Boolean?>(null)

        userRef.child(userId).child("name").setValue(newName).addOnSuccessListener {
            updateState.value = true
        }.addOnFailureListener {
            updateState.value = false
        }.addOnCanceledListener {
            updateState.value = false
        }
        return updateState
    }

    override fun addToBox(userId: String, boxId: String): MutableStateFlow<Boolean?> {
        val addState = MutableStateFlow<Boolean?>(null)

        val newBoxRef = userRef.child(userId).child("boxIdList").child(boxId)
        newBoxRef.child("isValid").setValue(true).addOnSuccessListener {
            newBoxRef.child("unseenCount").setValue(0).addOnSuccessListener {
                addState.value = true
            }.addOnFailureListener {
                addState.value = false
            }.addOnCanceledListener {
                addState.value = false
            }
        }.addOnFailureListener {
            addState.value = false
        }.addOnCanceledListener {
            addState.value = false
        }

        return addState
    }
    override fun getBoxIdList(userId: String): MutableStateFlow<List<String>> {
        val boxId = MutableStateFlow(emptyList<String>())

        userRef.child(userId).child("boxIdList").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var newBoxId = mutableListOf<String>()

                snapshot.children.forEach {
                    if (it.child("isValid").value == true) {
                        newBoxId.add(it.key.toString())
                    }
                }

                boxId.update { newBoxId }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ChatApp", "getBoxId Cancelled")
            }

        })
        return boxId
    }

    override fun getUserListByBoxId(boxId: String): MutableStateFlow<List<User>> {
        val resultList = MutableStateFlow(emptyList<User>())

        userRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var newList = mutableListOf<User>()
                snapshot.children.forEach {userItem ->
                    if (!userItem.child("boxIdList").children.none { it.key == boxId && it.child("isValid").value == true }) {
                        val id = userItem.key!!
                        val name = userItem.child("name").value.toString()
                        val avatar = userItem.child("avatar").value.toString()
                        val isActive = userItem.child("isActive").value.toString().toBoolean()
                        val listIdBox = mutableListOf<String>()
                        val newUser = User(id, name, avatar, isActive, listIdBox)
                        newList.add(newUser)
                    }
                }
                resultList.update { newList }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ChatApp", "getUserListByBoxId Cancelled")
            }
        })

        return resultList
    }

    override fun removeBox(userId: String, boxId: String): MutableStateFlow<Boolean?> {
        val resultState = MutableStateFlow<Boolean?>(null)
        userRef.child(userId).child("boxIdList").child(boxId).removeValue().addOnSuccessListener {
            resultState.value = true
        }.addOnFailureListener {
            resultState.value = false
        }.addOnCanceledListener {
            resultState.value = false
        }
        return resultState
    }

    override fun listenerCurrentUserRemoved(userId: String, boxId: String): MutableStateFlow<Boolean> {
        val resultState = MutableStateFlow(true)
        userRef.child(userId).child("boxIdList").child(boxId).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                resultState.value = snapshot.child("isValid").value.toString().toBoolean()
            }

            override fun onCancelled(error: DatabaseError) {
                resultState.value = false
            }

        })
        return resultState
    }

    override fun getBoxOnlineState(currentUserId: String, boxId: String): MutableStateFlow<Boolean?> {
        val resultOnlineState = MutableStateFlow<Boolean?>(null)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                resultOnlineState.value = false
                snapshot.children.forEach userForEach@{ user ->
                    user.child("boxIdList").children.forEach {box ->
                        val boxState = box.value.toString().toBoolean()
                        if (boxState) {
                            if (box.key == boxId && user.key != currentUserId ) {
                                val userState = user.child("active").value.toString().toBoolean()
                                if (userState) {
                                    resultOnlineState.value = true
                                    return@userForEach
                                }
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ChatApp", "getBoxOnlineState Cancelled")
            }
        })
        return resultOnlineState
    }

    override fun setUserUnseenCount(
        userId: String,
        boxId: String,
        number: Int
    ) {
        val unseenRef = userRef.child(userId).child("boxIdList").child(boxId).child("unseenCount")
        unseenRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(unseenCountNode: DataSnapshot) {
                val currentCount = unseenCountNode.value.toString().toInt()
                unseenRef.setValue(currentCount + 1)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ChatApp", "unseenRef Cancelled")
            }
        })
    }

    override fun resetUserUnseenCount(userId: String, boxId: String) {
        val unseenRef = userRef.child(userId).child("boxIdList").child(boxId).child("unseenCount")

        unseenRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                unseenRef.setValue(0)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ChatApp", "resetUserUnseenCount Cancelled")
            }
        })
    }

    override fun getUserUnseenCountList(userId: String): MutableStateFlow<List<Map<String, String>>> {
        val resultList = MutableStateFlow(emptyList<Map<String, String>>())
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(userListNode: DataSnapshot) {
                userListNode.children.forEach { user ->
                    if (user.key == userId) {
                        val newList = mutableListOf<Map<String, String>>()
                        user.child("boxIdList").children.forEach {boxNode ->
                            val count = boxNode.child("unseenCount").value.toString()
                            val bId = boxNode.key!!
                            if (count != "0") {
                                newList.add(mapOf(bId to count))
                            }
                        }
                        resultList.update { newList }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ChatApp", "getUserUnseenCountList Cancelled")
            }
        })
        return resultList
    }
}