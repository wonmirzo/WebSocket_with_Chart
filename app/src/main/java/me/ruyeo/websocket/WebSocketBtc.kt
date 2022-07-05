package me.ruyeo.websocket

import android.util.Log
import com.google.gson.Gson
import me.ruyeo.websocket.model.BitCoin
import me.ruyeo.websocket.model.DataSend
import okhttp3.*
import okio.ByteString

class WebSocketBtc {

    private lateinit var mWebSocket: WebSocket
    private lateinit var socketListener: SocketListener
    private  var gson = Gson()

    fun connectToSocket(currency: String) {
        val client = OkHttpClient()

        val request: Request = Request.Builder().url("wss://ws.bitstamp.net").build()
        client.newWebSocket(request,object : WebSocketListener(){
            override fun onOpen(webSocket: WebSocket, response: Response) {
                mWebSocket = webSocket
                webSocket.send(gson.toJson(Currency("bts:subscribe", DataSend(currency))))
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("MainActivity", "$text")
                val bitCoin = gson.fromJson(text, BitCoin::class.java)
                socketListener.onSuccess(bitCoin)
            }
            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                Log.d("MainActivity","$bytes")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("MainActivity","$reason")
            }
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("MainActivity","$reason")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.d("MainActivity","${t.localizedMessage} and $response")
                socketListener.onFailure(t.localizedMessage)
            }
        })
        client.dispatcher.executorService.shutdown()


    }

    fun socketListener(socketListener: SocketListener){
        this.socketListener = socketListener
    }
}

interface SocketListener{
    fun onSuccess(bitCoin: BitCoin)
    fun onFailure(message: String)
}