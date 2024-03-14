package com.example.testingsockets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.testingsockets.data.DroneInit
import com.example.testingsockets.data.OutputJSON
import com.example.testingsockets.ui.theme.TestingSocketsTheme
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val gson: Gson = GsonBuilder().disableHtmlEscaping().create()

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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(onClick = {
                                coroutineScope.launch {
                                    withContext(Dispatchers.IO) {
                                        testInit()
                                    }
                                }
                            }) {
                                Text(text = "Init")
                            }
                            Button(onClick = {
                                coroutineScope.launch {
                                    withContext(Dispatchers.IO) {
                                        testSend()
                                    }
                                }
                            }) {
                                Text(text = "Send 2")
                            }
                            Button(onClick = {
                                coroutineScope.launch {
                                    withContext(Dispatchers.IO) {
                                        testSend()
                                    }
                                }
                            }) {
                                Text(text = "Send 3")
                            }
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
    return BitmapFactory.decodeResource(con.resources, R.drawable.img_meal_two)
}

fun testObjDetect(bitmap: Bitmap) {
    val returnedResults = ObjectDetect.runObjectDetection(bitmap)
    if (returnedResults != null) {
        for (result in returnedResults) {
            val output =
                OutputJSON(3.1, 4.1, 5.2, result.label, Base64Util.bitmapToBase64(result.img))
            SocketHelper.sendJSON(output)
        }
    }
}

fun testSend() {
    SocketHelper.sendMessage("Testing!")
}

fun testInit() {
    val droneInit = DroneInit(43.860930, -78.851260, 0.0, 45.0)
    val jsonString = gson.toJson(droneInit)
    Log.d("DroneInit",jsonString)
    SocketHelper.sendMessage(jsonString)
}

suspend fun testConnect() {
    SocketHelper.start(9999, "192.168.2.46")
}

fun close() {
    SocketHelper.close()
}