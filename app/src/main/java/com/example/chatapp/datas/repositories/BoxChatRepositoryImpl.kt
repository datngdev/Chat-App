package com.example.chatapp.datas.repositories

import android.util.Log
import com.example.chatapp.datas.models.BoxChat
import com.example.chatapp.datas.models.Message
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class BoxChatRepositoryImpl : BoxChatRepository {
    private val db = Firebase.database
    private val boxRef = db.getReference("boxChats")
    private val userRef = db.getReference("users")

    override fun createBoxChat(userId: String, boxName: String): StateFlow<Boolean?> {
        val createBoxStatus = MutableStateFlow<Boolean?>(null)

        val defaultAvatar = "default.jpg"
        val newBoxId = boxRef.push().key!!
        var newBoxChat = BoxChat(newBoxId, boxName, defaultAvatar, "Box Chat Created")

        boxRef.child(newBoxId).setValue(newBoxChat).addOnSuccessListener {

            userRef.child(userId).child("boxIdList").child(newBoxId).setValue(true).addOnSuccessListener {
                createBoxStatus.value = true
            }.addOnFailureListener {
                createBoxStatus.value = false
            }
        }.addOnFailureListener {
            createBoxStatus.value = false
        }
        return createBoxStatus
    }

    override fun getBox(boxId: List<String>): MutableStateFlow<List<BoxChat>> {
        val boxList = MutableStateFlow(emptyList<BoxChat>())
        boxRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newBox = mutableListOf<BoxChat>()
                snapshot.children.forEach {boxItem ->
                    if (boxId.contains(boxItem.key)) {
                        val boxId = boxItem.key.toString()
                        val boxName = boxItem.child("name").value.toString()
                        val boxAvatar = boxItem.child("avatar").value.toString()
                        var boxMess = boxItem.child("lastMess").value.toString()
                        if (boxMess.isNullOrEmpty()) {
                            boxMess = ""
                        }

                        val newItem = BoxChat(boxId, boxName, boxAvatar, boxMess)

                        newBox.add(newItem)
                    }
                }

                boxList.update { newBox }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ChatApp", "getBox Cancelled")
            }

        })
        return boxList
    }

    override fun sendMess(userId: String, boxId: String, data: String) {
        val newMess = Message(userId, data)

        val messKey = boxRef.child(boxId).child("messages").push().key!!
        boxRef.child(boxId).child("messages").child(messKey)
            .setValue(newMess)
            .addOnFailureListener {
                Log.d("sendMess", "False")
            }
        boxRef.child(boxId).child("lastMess")
            .setValue(data)
            .addOnFailureListener {
                Log.d("ChatApp", "set lastMess Failure")
            }
    }

    override fun getMess(userId: String, boxId: String): MutableStateFlow<List<Message>> {
        val messList = MutableStateFlow(emptyList<Message>())
        boxRef.child(boxId).child("messages").addValueEventListener( object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var newList = mutableListOf<Message>()
                snapshot.children.forEach {
                    val sender = it.child("sender").value.toString()
                    val data = it.child("data").value.toString()

                    val newMess = Message(sender, data)
                    newList.add(newMess)
                }
                messList.update { newList }
                Log.d("messList repo", messList.value.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ChatApp", "getMess Cancelled")
            }

        })
        return messList
    }

    fun getBoxDetail(boxId: String): MutableStateFlow<List<String>> {
        val boxDetail = MutableStateFlow(emptyList<String>())

        boxRef.child(boxId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var boxDetail = mutableListOf<String>()

                val name = snapshot.child("name").value.toString()

                boxDetail.add(name)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ChatApp", "Get Box Detail Cancelled")
            }

        })
        return boxDetail
    }

}