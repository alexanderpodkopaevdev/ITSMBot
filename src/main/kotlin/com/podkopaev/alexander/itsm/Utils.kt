package com.podkopaev.alexander.itsm

import com.podkopaev.alexander.itsm.scenario.MainScenario.LOG
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking

fun convertJsonToList(responseString: String): List<String> {
    return responseString.replace("[", "").replace("]", "").split(",")
}

fun convertJsonToNormal(responseString: String): String {
    return responseString.substring(1,responseString.length-1)
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