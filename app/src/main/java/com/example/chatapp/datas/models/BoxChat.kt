package com.example.chatapp.datas.models

data class BoxChat (
    val id: String,
    val name: String,
    val avatar: String,
    var lastMess: String,
    var lastSendTime: String
)