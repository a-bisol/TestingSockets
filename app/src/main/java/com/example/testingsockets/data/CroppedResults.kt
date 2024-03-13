package com.example.testingsockets.data

import android.graphics.Bitmap

data class CroppedResults(
    val img: Bitmap,
    val label: String,
    val score: Int
)
