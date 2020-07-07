package com.podkopaev.alexander.itsm

import com.justai.jaicf.channel.http.httpBotRouting
import com.justai.jaicf.channel.telegram.TelegramChannel
import com.justai.jaicf.channel.yandexalice.AliceChannel
import com.podkopaev.alexander.itsm.naumen.NaumenData.OAUTH_TOKEN
import com.podkopaev.alexander.itsm.naumen.NaumenData.TELEGRAM_TOKEN
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

/*
fun main() {
    embeddedServer(Netty, System.getenv("PORT")?.toInt() ?: 8080) {
        routing {
            httpBotRouting("/" to AliceChannel(
                skill,
                System.getenv("OAUTH_TOKEN") ?: OAUTH_TOKEN
            ))
        }
    }.start(wait = true)

}*/
