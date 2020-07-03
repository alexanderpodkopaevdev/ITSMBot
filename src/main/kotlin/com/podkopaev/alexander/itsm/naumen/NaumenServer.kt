package com.podkopaev.alexander.itsm.naumen

import com.google.gson.Gson
import com.podkopaev.alexander.itsm.globalitsm.ItsmServer
import com.podkopaev.alexander.itsm.convertJsonToList
import com.podkopaev.alexander.itsm.naumen.model.NaumenCall
import com.podkopaev.alexander.itsm.naumen.model.NaumenKB
import com.podkopaev.alexander.itsm.naumen.model.NaumenOU
import com.podkopaev.alexander.itsm.requestToServer
import com.podkopaev.alexander.itsm.scenario.LOG

class NaumenServer : ItsmServer {

    override fun createCall(input: String?): Any {
        val fqn = "serviceCall"
        val attr = """
{"title":"new",
"metaClass":"serviceCall${'$'}call", 
"description":"$input",
"shortDescr":"$input",
"descriptionRTF":"Обращение создано из интерфейса Алисы. 
$input",
"agreement":"agreement${'$'}605301",
"service":"slmService${'$'}605005"}
        """
        val newUrl =
            "${NaumenData.SERVER_URL}/services/rest/create/${fqn}/${attr}?${NaumenData.ACCESS_KEY}"

        if (LOG) println(newUrl)

        val responseString = requestToServer(newUrl)

        return responseString.substring(0, if (responseString.length < 100) responseString.length - 1 else 100)

    }

    override fun findOU(text: String?): List<NaumenOU.NaumenOuInfo> {
        val findOuUrl =
            "${NaumenData.SERVER_URL}/services/rest/search/ou/%7Btitle:\"${text?.capitalize()}\"%7D/?${NaumenData.ACCESS_KEY}"
        var responseString = requestToServer(findOuUrl)
        if (LOG) println(responseString)

        val listOus = convertJsonToList(responseString)
        val ous = mutableListOf<NaumenOU.NaumenOuInfo>()
        for (ou in listOus) {
            if (ou.isNullOrEmpty()) break
            val getOuUrl =
                "${NaumenData.SERVER_URL}/services/rest/get/${ou.replace(
                    "\"",
                    ""
                )}?${NaumenData.ACCESS_KEY}"
            responseString = requestToServer(getOuUrl)
            if (LOG) println(responseString)
            ous.add(Gson().fromJson(responseString, NaumenOU.NaumenOuInfo::class.java))
        }
        return ous
    }

    override fun findArticle(text: String?): List<NaumenKB.NaumenKbInfo> {
        val findArticleUrl =
            "${NaumenData.SERVER_URL}/services/rest/search/KB\$KBArticle/%7Btitle:\"${text}\"%7D/?${NaumenData.ACCESS_KEY}"
        var responseString = requestToServer(findArticleUrl)
        val listArticles = convertJsonToList(responseString)
        val articles = mutableListOf<NaumenKB.NaumenKbInfo>()
        for (article in listArticles) {
            if (article.isNullOrEmpty()) break
            val getArticleUrl =
                "${NaumenData.SERVER_URL}/services/rest/get/${article.replace(
                    "\"",
                    ""
                )}?${NaumenData.ACCESS_KEY}"
            responseString = requestToServer(getArticleUrl)
            articles.add(Gson().fromJson(responseString, NaumenKB.NaumenKbInfo::class.java))
        }
        return articles
    }

    override fun findCall(text: String?) : List<NaumenCall.NaumenServiceCall> {
        val findCallUrl =
            "${NaumenData.SERVER_URL}/services/rest/search/serviceCall/%7Bnumber:\"${text}\"%7D/?${NaumenData.ACCESS_KEY}"
        var responseString = requestToServer(findCallUrl)
        val listCalls = convertJsonToList(responseString)
        val calls = mutableListOf<NaumenCall.NaumenServiceCall>()
        for (call in listCalls) {
            if (call.isNullOrEmpty()) break
            val getArticleUrl =
                "${NaumenData.SERVER_URL}/services/rest/get/${call.replace(
                    "\"",
                    ""
                )}?${NaumenData.ACCESS_KEY}"
            responseString = requestToServer(getArticleUrl)
            calls.add(Gson().fromJson(responseString, NaumenCall.NaumenServiceCall::class.java))
        }
        return calls
        // https://softline-presale.itsm365.com/sd/services/rest/search/serviceCall/%7Bnumber:80%7D/?accessKey=589caa54-2528-45c3-b16c-86be9f2081c5
        // https://softline-presale.itsm365.com/sd/services/rest/get/serviceCall$2377209/?accessKey=589caa54-2528-45c3-b16c-86be9f2081c5

    }
}