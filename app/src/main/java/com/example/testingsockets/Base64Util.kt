package com.example.testingsockets

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object Base64Util {
    @OptIn(ExperimentalEncodingApi::class)
    fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream)

        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encode(byteArray)
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun Base64tobitmap(b64: String): Bitmap {
        val imageBytes = Base64.decode(b64, 0)
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
}