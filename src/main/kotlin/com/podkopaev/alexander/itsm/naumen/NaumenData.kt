package com.podkopaev.alexander.itsm.naumen

object NaumenData {
    val SERVER_URL = System.getenv("SERVER_URL")// ?: NaumenDataHide.SERVER_URL
    val ACCESS_KEY = System.getenv("ACCESS_KEY")// ?: NaumenDataHide.ACCESS_KEY
    val TELEGRAM_TOKEN = System.getenv("TELEGRAM_TOKEN") //?: NaumenDataHide.TELEGRAM_TOKEN
    val OAUTH_TOKEN = System.getenv("OAUTH_TOKEN")// ?: NaumenDataHide.OAUTH_TOKEN
}