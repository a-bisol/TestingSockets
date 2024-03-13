package com.example.testingsockets

import android.util.Log
import com.example.testingsockets.data.OutputJSON
import com.example.testingsockets.data.Points
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket


object SocketHelper {
    private lateinit var clientSocket: Socket
    private lateinit var output: PrintWriter
    private lateinit var input: BufferedReader
    private var gson = GsonBuilder().disableHtmlEscaping().create()

    suspend fun start(port: Int, ip: String) {
        try {
            withContext(Dispatchers.IO) {
                clientSocket = Socket(ip, port)
                output = PrintWriter(clientSocket.getOutputStream(), true)
                input = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
            }
        } catch (e: IOException) {
            Log.e("Start error", e.toString())
        } catch (e: Exception) {
            Log.e("Misc error", e.toString())
        }

        Log.d("Started", clientSocket.isConnected.toString())
        val sendStringObj = Points(
            "[[43.94554359908121, -78.89668560139657], [43.944835864796055, -78.89603759292302], [43.944804666633644, -78.8969562996474], [43.9455124009188, -78.89760432372148], [43.945528, -78.897145]]",
            50
        )
        sendMessage(gson.toJson(sendStringObj))
        receiver()
    }

    fun sendMessage(msg: String) {
        output.println(msg)
    }

    fun sendJSON(obj: OutputJSON) {
        val jsonString = gson.toJson(obj)
        output.println(jsonString)
    }

    private suspend fun receiver() {
        var msg: String
        try {
            withContext(Dispatchers.IO) {
                Log.d("Receiver", "Online")
                msg = input.readLine()
                Log.d("Msg received", msg)
                val pointsObj = gson.fromJson(msg,Points::class.java)
                val pointStr = pointsObj.points.substring(1,pointsObj.points.length-1)
                val points = pointStr.split("], [").toTypedArray()
                points[points.lastIndex]=points.last().substring(0,points.last().length-1)
                points[0]=points.first().substring(1,points.first().length)
                for (point in points) {
                    val tempPoints = point.split(", ")
                    val (x,y) = Pair(tempPoints[0].toDouble(), tempPoints[1].toDouble())
                    Log.d("Point Res", "First: $x Second: $y")
                }
                while (msg != null) {
                    msg = input.readLine()
                    Log.d("Msg received", msg)
//                    TODO
//                    Handle actions depending on input
                }
                Log.e("Receiver", "Server unreachable")
                output.close()
                clientSocket.close()
            }
        } catch (e: IOException) {
            Log.e("Error", e.toString())
        }
    }

    fun close() {
        output.close()
        input.close()
        clientSocket.close()
        Log.e("Close", "Closed socket")
    }
}