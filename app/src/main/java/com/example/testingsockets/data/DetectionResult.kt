package com.example.testingsockets.data

import android.graphics.RectF

data class DetectionResult(
    val boundingBox: RectF,
    val text: String,
    val score: String
)
