package com.example.testingsockets

import android.util.Log
import com.example.testingsockets.data.OutputJSON
import com.example.testingsockets.data.Points
import com.example.testingsockets.data.ServerComms
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
                Log.d("Msg",msg)
                while (msg != null) {
                    //Keeps reading line by line until null is received
                    //Studio lies and this cannot be replaced by while(true)
                    msg = input.readLine()
                    if (msg.contains("points")) {
                        parsePoints(msg)
                    } else if (msg.contains("uid")) {
                        parseFind(msg)
                    } else {
                        Log.d("Unknown message", msg)
                    }
                }
                Log.e("Receiver", "Server unreachable")
                output.close()
                clientSocket.close()
            }
        } catch (e: IOException) {
            Log.e("Error", e.toString())
        }
    }

    private fun parseFind(msg: String) {
        val findObj = gson.fromJson(msg, ServerComms::class.java)
        Log.d("ParseFind",findObj.x_cord.toString()+", "+findObj.y_cord.toString())
//        val location2D = LocationCoordinate2D(findObj.x_cord, findObj.y_cord)
//        FlightStateManager.setPriority(location2D)
        //TODO Compare findObj.image to current images
    }



    private fun parsePoints(msg: String) {
        val pointsObj = gson.fromJson(msg, Points::class.java)
//        FlightStateManager.setAltitudeTarget(pointsObj.altitude)
        Log.d("ParsePoints",pointsObj.altitude.toString())
        //Strips first set of []
        for (entry in pointsObj.points) {
            Log.d("SinglePoint",entry[0].toString()+", "+entry[1].toString())
        }
        Log.d("Name",pointsObj.drone_name)
        Log.d("Target",pointsObj.target)
    }

    fun close() {
        output.close()
        input.close()
        clientSocket.close()
        Log.e("Close", "Closed socket")
    }
}