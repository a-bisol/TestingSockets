package com.example.testingsockets

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.testingsockets.ui.theme.TestingSocketsTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val coroutineScope = rememberCoroutineScope()
            TestingSocketsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        Button(onClick = {
                            coroutineScope.launch {
                                withContext(Dispatchers.IO) {
                                    ObjectDetect.initDetector(this@MainActivity)
                                    testConnect()
                                }
                            }
                        }) {
                            Text(text = "Connect")
                        }
                        Button(onClick = {
                            coroutineScope.launch {
                                withContext(Dispatchers.IO) {
                                    testSend()
                                }
                            }
                        }) {
                            Text(text = "Send")
                        }
                        Button(onClick = {
                            coroutineScope.launch {
                                withContext(Dispatchers.IO) {
                                    close()
                                }
                            }
                        }) {
                            Text(text = "Close")
                        }
                        Button(onClick = {
                            val bitmap = resToBitmap(this@MainActivity)
                            coroutineScope.launch {
                                withContext(Dispatchers.IO) {
                                    setViewAndDetect(bitmap)
                                }
                            }
                        }) {
                            Text(text = "Detect")
                        }
                        Card(
                            modifier = Modifier.size(300.dp),
                            shape = RectangleShape
                        ) {
                            Image(
                                painterResource(R.drawable.img_meal_two),
                                contentDescription = "",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

suspend fun setViewAndDetect(bitmap: Bitmap) {
    withContext(Dispatchers.IO) {
        testObjDetect(bitmap)
    }
}

fun resToBitmap(con: Context): Bitmap {
    val bitmap = BitmapFactory.decodeResource(con.resources, R.drawable.img_meal_two)
    Log.d("Bitmap test", bitmap.width.toString())
    val resized = Bitmap.createScaledBitmap(
        bitmap, (bitmap.width * 0.2).toInt(),
        (bitmap.height * 0.2).toInt(), true
    )
    Log.d("Resize test",resized.width.toString())
    return resized
}

fun testObjDetect(bitmap: Bitmap) {
    val resultToDisplay = ObjectDetect.runObjectDetection(bitmap)
    SocketHelper.sendMessage(resultToDisplay)
}

fun testSend() {
//    SocketHelper.sendMessage("Testing!")
    SocketHelper.sendJSON()
}

suspend fun testConnect() {
    SocketHelper.start(60000, "192.168.1.140")
}

fun close() {
    SocketHelper.close()
}