package com.example.chatapp.datas.models

import java.sql.Timestamp

data class Message (
    val sender: String,
    val data: String,
    val type: Int,
    val sendTime: String?
)