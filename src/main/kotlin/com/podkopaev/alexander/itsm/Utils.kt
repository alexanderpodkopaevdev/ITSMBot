package com.podkopaev.alexander.itsm

import com.podkopaev.alexander.itsm.scenario.LOG
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking

fun convertJsonToList(responseString: String): List<String> {
    return responseString.replace("[", "").replace("]", "").split(",")
}

fun requestToServer(url: String): String {
    var response = ""
    try {
        runBlocking {
            val client = HttpClient()
            response = client.get(url)
            client.close()
        }
        return response
    } catch (e: Exception) {
        if (LOG) println(e.message)
        return e.message.toString()
    }

}