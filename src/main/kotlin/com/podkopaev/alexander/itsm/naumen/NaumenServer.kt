package com.podkopaev.alexander.itsm.naumen

import com.google.gson.Gson
import com.podkopaev.alexander.itsm.convertJsonToList
import com.podkopaev.alexander.itsm.convertJsonToNormal
import com.podkopaev.alexander.itsm.globalitsm.ItsmServer
import com.podkopaev.alexander.itsm.globalitsm.model.ItsmEmployee
import com.podkopaev.alexander.itsm.naumen.model.NaumenCall
import com.podkopaev.alexander.itsm.naumen.model.NaumenEmployee
import com.podkopaev.alexander.itsm.naumen.model.NaumenKB
import com.podkopaev.alexander.itsm.naumen.model.NaumenOU
import com.podkopaev.alexander.itsm.requestToServer
import com.podkopaev.alexander.itsm.scenario.MainScenario.LOG
import com.podkopaev.alexander.itsm.scenario.MainScenario.userUUID

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
"service":"slmService${'$'}605005",
"author":"$userUUID",
"clientEmployee":"$userUUID"}
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

    override fun findCall(text: String?): List<NaumenCall.NaumenServiceCall> {
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

    }

    override fun getUserByTelegramID(telegramId: Long?): NaumenEmployee.NaumenEmployeeInfo? {

        val findUser =
            "${NaumenData.SERVER_URL}/services/rest/find/employee/%7Btelegram:\"${telegramId}\"%7D/?${NaumenData.ACCESS_KEY}"
        val responseString = requestToServer(findUser)
        if (LOG) println(responseString)

        if (LOG) println(convertJsonToNormal(responseString))
        val user = Gson().fromJson(convertJsonToNormal(responseString), NaumenEmployee.NaumenEmployeeInfo::class.java)
        if (LOG) println(user)
        return user

    }

    override fun getUserByAliceId(userId: String?): ItsmEmployee.ItsmEmployeeInfo? {
        val findUser =
            "${NaumenData.SERVER_URL}/services/rest/find/employee/%7Btelegram:\"${userId}\"%7D/?${NaumenData.ACCESS_KEY}"
        val responseString = requestToServer(findUser)
        if (LOG) println(responseString)

        if (LOG) println(convertJsonToNormal(responseString))
        val user = Gson().fromJson(convertJsonToNormal(responseString), NaumenEmployee.NaumenEmployeeInfo::class.java)
        if (LOG) println(user)
        return user
    }

    override fun getUserBySdId(userId: String?): ItsmEmployee.ItsmEmployeeInfo? {
        val findUser =
            "${NaumenData.SERVER_URL}/services/rest/find/employee/%7Buuid:\"${userId}\"%7D/?${NaumenData.ACCESS_KEY}"
        val responseString = requestToServer(findUser)
        if (LOG) println(responseString)

        if (LOG) println(convertJsonToNormal(responseString))
        val user = Gson().fromJson(convertJsonToNormal(responseString), NaumenEmployee.NaumenEmployeeInfo::class.java)
        if (LOG) println(user)
        return user

    }

    override fun setTelegramIdForUser(sdUser: ItsmEmployee.ItsmEmployeeInfo?, telegramId: Long?): String {
        val attr = """
{"telegram":"$telegramId"}
        """
        val newUrl =
            "${NaumenData.SERVER_URL}/services/rest/edit/${sdUser?.UUID}/${attr}?${NaumenData.ACCESS_KEY}"

        if (LOG) println(newUrl)

        val responseString = requestToServer(newUrl)

        return responseString.substring(0, if (responseString.length < 100) responseString.length - 1 else 100)

    }

    override fun setAliceIdForUser(sdUser: ItsmEmployee.ItsmEmployeeInfo?, AliceId: String?): String {
        val attr = """
{"telegram":"$AliceId"}
        """
        val newUrl =
            "${NaumenData.SERVER_URL}/services/rest/edit/${sdUser?.UUID}/${attr}?${NaumenData.ACCESS_KEY}"

        if (LOG) println(newUrl)

        val responseString = requestToServer(newUrl)

        return responseString.substring(0, if (responseString.length < 100) responseString.length - 1 else 100)

    }

    override fun getUserByPhone(phoneNumber: String?): NaumenEmployee.NaumenEmployeeInfo? {
        val findUser =
            "${NaumenData.SERVER_URL}/services/rest/find/employee/%7BmobilePhoneNumber:\"${phoneNumber}\"%7D/?${NaumenData.ACCESS_KEY}"
        val responseString = requestToServer(findUser)
        return Gson().fromJson(convertJsonToNormal(responseString), NaumenEmployee.NaumenEmployeeInfo::class.java)
    }
}