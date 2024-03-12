package com.example.testingsockets

import android.util.Log
import com.google.gson.Gson
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
    private var gson = Gson()

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

    fun sendJSON() {
        val testSend = Test(42,"Testing 42nd send")
        val jsonString = gson.toJson(testSend)
        output.println(jsonString)
    }

    private suspend fun receiver() {
        var msg: String
        try {
            withContext(Dispatchers.IO) {
                Log.d("Receiver","Online")
                msg = input.readLine()
                while (msg != null) {
                    Log.d("Msg", msg)
                    msg = input.readLine()
                }
                Log.e("Uhoh Stinky", "Server unreachable")
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
        Log.e("Close","Closed socket")
    }
}