package com.example.chatapp.datas.repositories

import android.net.Uri
import android.util.Log
import com.example.chatapp.datas.models.BoxChat
import com.example.chatapp.datas.models.Message
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class BoxRepositoryImpl : BoxRepository {
    private val db = Firebase.database
    private val storage = Firebase.storage

    private val boxRef = db.getReference("boxChats")
    private val boxAvatar = storage.getReference("BoxAvatar")

    override fun createBoxChat(boxName: String, boxAvatarUrl: String): MutableStateFlow<String?> {
        val createBoxStatus = MutableStateFlow<String?>(null)

            val newBoxId = boxRef.push().key!!
            var newBoxChat = BoxChat(newBoxId, boxName, boxAvatarUrl, "Box Chat Created", "")

            boxRef.child(newBoxId).setValue(newBoxChat).addOnSuccessListener {
                createBoxStatus.value = newBoxId
            }.addOnFailureListener {
                createBoxStatus.value = ""
            }.addOnCanceledListener {
                createBoxStatus.value = ""
            }
        return createBoxStatus
    }

    override fun upLoadBoxAvatar(boxId: String, imageUri: Uri): MutableStateFlow<Boolean?> {
        val uploadState = MutableStateFlow<Boolean?>(null)
        val boxAvatarRef = boxAvatar.child("$boxId.jpg")
        boxAvatarRef.putFile(imageUri).addOnSuccessListener {
            uploadState.value = true
        }.addOnFailureListener {
            uploadState.value = false
        }.addOnCanceledListener {
            uploadState.value = false
        }

        return uploadState
    }

    override fun getBoxAvatarDownloadUrl(boxId: String): MutableStateFlow<String?> {
        val resultUrl = MutableStateFlow<String?>(null)

        val boxAvatarRef = boxAvatar.child("$boxId.jpg")
        boxAvatarRef.downloadUrl.addOnSuccessListener { avatarUrl ->
            resultUrl.value = avatarUrl.toString()
        }.addOnFailureListener {
            resultUrl.value = ""
        }

        return resultUrl
    }

    override fun setName(boxId: String, newName: String): MutableStateFlow<Boolean?> {
        val updateState = MutableStateFlow<Boolean?>(null)

        boxRef.child(boxId).child("name").setValue(newName).addOnSuccessListener {
            updateState.value = true
        }.addOnFailureListener {
            updateState.value = false
        }.addOnCanceledListener {
            updateState.value = false
        }
        return updateState
    }

    override fun setBoxAvatarUrl(boxId: String, avatarUrl: String):  MutableStateFlow<Boolean?>{
        val updateState = MutableStateFlow<Boolean?>(null)

        boxRef.child(boxId).child("avatar").setValue(avatarUrl).addOnSuccessListener {
            updateState.value = true
        }.addOnFailureListener {
            updateState.value = false
        }.addOnCanceledListener {
            updateState.value = false
        }

        return updateState
    }

    override fun getBoxName(boxId: String): MutableStateFlow<String> {
        val boxName = MutableStateFlow("")

        boxRef.child(boxId).child("name").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                boxName.value = snapshot.value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ChatApp", "getBoxName Cancelled")
            }
        })
        return boxName
    }

    override fun getBoxAvatarUrl(boxId: String): MutableStateFlow<String?> {
        val avatarUrl = MutableStateFlow<String?>(null)
        boxRef.child(boxId).child("avatar").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                avatarUrl.value = snapshot.value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ChatApp", "getBoxAvatarUrl Cancelled")
            }

        })

        return avatarUrl
    }

    override fun getBoxByIdList(boxIdList: List<String>): MutableStateFlow<List<BoxChat>> {
        val resultBoxList = MutableStateFlow(emptyList<BoxChat>())

        boxRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newBoxList = mutableListOf<BoxChat>()

                snapshot.children.forEach {boxItem ->
                    if (boxIdList.contains(boxItem.key)) {
                        val id = boxItem.key.toString()
                        val name = boxItem.child("name").value.toString()
                        val avatarUrl = boxItem.child("avatar").value.toString()
                        var lastMess = boxItem.child("lastMess").value.toString()
                        if (lastMess.isNullOrEmpty()) { lastMess = "" }
                        val lastSendTime = boxItem.child("lastSendTime").value.toString()

                        val newItem = BoxChat(id, name, avatarUrl, lastMess, lastSendTime)

                        newBoxList.add(newItem)
                    }
                }

                resultBoxList.update { newBoxList }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ChatApp", "getBox Cancelled")
            }

        })
        return resultBoxList
    }
    override fun sendMess(userId: String, boxId: String, data: String, type: Int): MutableStateFlow<Boolean?> {
        val sendState = MutableStateFlow<Boolean?>(null)
        val newMess = Message(userId, data, type, null)

        val messKey = boxRef.child(boxId).child("messages").push().key!!

        val messRef = boxRef.child(boxId).child("messages").child(messKey)
        messRef.setValue(newMess).addOnSuccessListener {
            messRef.child("sendTime").setValue(ServerValue.TIMESTAMP).addOnSuccessListener {
                sendState.value = true
            }.addOnFailureListener {
                sendState.value = true
            }
        }.addOnFailureListener {
            sendState.value = false
        }

        val lastMess = when (type) {
            1 -> {
                data
            }
            2 -> {
                "$userId sent a image"
            }
            else -> {
                ""
            }
        }

        boxRef.child(boxId).child("lastSendTime").setValue(ServerValue.TIMESTAMP).addOnFailureListener {
            Log.d("ChatApp", "set lastSendTime Failure")
        }

        boxRef.child(boxId).child("lastMess").setValue(lastMess).addOnFailureListener {
            Log.d("ChatApp", "set lastMess Failure")
        }

        return sendState
    }

    override fun getMessList(userId: String, boxId: String): MutableStateFlow<List<Message>> {
        val resultMessList = MutableStateFlow(emptyList<Message>())

        boxRef.child(boxId).child("messages").addValueEventListener( object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var newMessList = mutableListOf<Message>()

                snapshot.children.forEach {
                    val sender = it.child("sender").value.toString()
                    val data = it.child("data").value.toString()
                    val type = it.child("type").value.toString().toInt()
                    val sendTime = it.child("sendTime").value.toString()

                    val newMess = Message(sender, data, type, sendTime)
                    newMessList.add(newMess)
                }
                resultMessList.update { newMessList }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ChatApp", "getMess Cancelled")
            }

        })
        return resultMessList
    }

    override fun setAdmin(userId: String, boxId: String): MutableStateFlow<Boolean?> {
        val resultState = MutableStateFlow<Boolean?>(null)
        boxRef.child(boxId).child("adminIdList").child(userId).setValue(true).addOnSuccessListener {
            resultState.value = true
        }.addOnFailureListener {
            resultState.value = false
        }.addOnCanceledListener {
            resultState.value = false
        }
        return resultState
    }

    override fun removeAdmin(userId: String, boxId: String): MutableStateFlow<Boolean?> {
        val resultState = MutableStateFlow<Boolean?>(null)
        boxRef.child(boxId).child("adminIdList").child(userId).setValue(false).addOnSuccessListener {
            resultState.value = true
        }.addOnFailureListener {
            resultState.value = false
        }.addOnCanceledListener {
            resultState.value = false
        }
        return resultState
    }

    override fun getAdminIdList(boxId: String): MutableStateFlow<List<String>> {
        val resultIdList = MutableStateFlow(emptyList<String>())
        boxRef.child(boxId).child("adminIdList").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var newIdList = mutableListOf<String>()
                snapshot.children.forEach {idItem ->
                    if (idItem.value == true) {
                        newIdList.add(idItem.key!!)
                    }
                }
                resultIdList.update { newIdList }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ChatApp", "getAdminIdList Cancelled")
            }
        })
        return resultIdList
    }

    override fun uploadImage(boxId: String, image: Uri): MutableStateFlow<String?> {
        val resultDownloadUrl = MutableStateFlow<String?>(null)
        val fileExtension = ".jpg"
        val randomFileName = UUID.randomUUID().toString() + fileExtension
        val currentFileRef = storage.getReference(boxId).child(randomFileName)

        currentFileRef.putFile(image).addOnSuccessListener {
            currentFileRef.downloadUrl.addOnSuccessListener { imageUrl ->
                resultDownloadUrl.update { imageUrl.toString() }
            }
        }
        return resultDownloadUrl
    }
}