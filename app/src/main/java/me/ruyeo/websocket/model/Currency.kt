package me.ruyeo.websocket

import me.ruyeo.websocket.model.DataSend

data class Currency(
    var event: String,
    var data: DataSend
)