package com.example.testingsockets.data

data class ServerComms(
    val drone_name: String,
    val x_cord: Double,
    val y_cord: Double,
    val alt: Double?,
    val image: String?,
    val uid: Int?
)