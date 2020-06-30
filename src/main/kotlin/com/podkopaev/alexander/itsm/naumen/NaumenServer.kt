package com.podkopaev.alexander.itsm.naumen

import com.google.gson.Gson
import com.podkopaev.alexander.itsm.globalitsm.ItsmServer
import com.podkopaev.alexander.itsm.convertJsonToList
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

    override fun findOU(text: String?): List<NaumenOU.OuInfo> {
        val findOuUrl =
            "${NaumenData.SERVER_URL}/services/rest/search/ou/%7Btitle:\"${text?.capitalize()}\"%7D/?${NaumenData.ACCESS_KEY}"
        var responseString = requestToServer(findOuUrl)
        val listOus = convertJsonToList(responseString)
        val ous = mutableListOf<NaumenOU.OuInfo>()
        for (ou in listOus) {
            if (ou.isNullOrEmpty()) break
            val getOuUrl =
                "${NaumenData.SERVER_URL}/services/rest/get/${ou.replace(
                    "\"",
                    ""
                )}?${NaumenData.ACCESS_KEY}"
            responseString = requestToServer(getOuUrl)
            ous.add(Gson().fromJson(responseString, NaumenOU.OuInfo::class.java))
        }
        return ous
    }

    override fun findArticle(text: String?): List<NaumenKB.KbInfo> {
        val findArticleUrl =
            "${NaumenData.SERVER_URL}/services/rest/search/KB\$KBArticle/%7Btitle:\"${text}\"%7D/?${NaumenData.ACCESS_KEY}"
        var responseString = requestToServer(findArticleUrl)
        val listArticles = convertJsonToList(responseString)
        val articles = mutableListOf<NaumenKB.KbInfo>()
        for (article in listArticles) {
            if (article.isNullOrEmpty()) break
            val getArticleUrl =
                "${NaumenData.SERVER_URL}/services/rest/get/${article.replace(
                    "\"",
                    ""
                )}?${NaumenData.ACCESS_KEY}"
            responseString = requestToServer(getArticleUrl)
            articles.add(Gson().fromJson(responseString, NaumenKB.KbInfo::class.java))
        }
        return articles
    }
}