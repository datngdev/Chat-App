package com.example.chatapp.datas.models

data class User(
    val id: String,
    val name: String,
    val avatar: String,
    val isActive: Boolean,
    val boxIdList: MutableList<String>
)