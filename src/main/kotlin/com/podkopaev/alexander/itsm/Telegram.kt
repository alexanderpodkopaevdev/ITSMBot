package com.podkopaev.alexander.itsm

import com.justai.jaicf.channel.telegram.TelegramChannel
import com.podkopaev.alexander.itsm.naumen.NaumenData

fun main() {
    TelegramChannel(skill, NaumenData.TELEGRAM_TOKEN).run()
}