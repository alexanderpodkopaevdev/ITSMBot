package com.podkopaev.alexander.naumen.scenario

import com.google.gson.Gson
import com.justai.jaicf.channel.yandexalice.AliceEvent
import com.justai.jaicf.channel.yandexalice.alice
import com.justai.jaicf.channel.yandexalice.api.alice
import com.justai.jaicf.channel.yandexalice.api.model.Button
import com.justai.jaicf.model.scenario.Scenario
import com.podkopaev.alexander.naumen.Data.ACCESS_KEY
import com.podkopaev.alexander.naumen.Data.SERVER_URL
import com.podkopaev.alexander.naumen.model.OU.OU
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking

val LOG: Boolean = true

object MainScenario : Scenario() {
    init {
        state("main") {
            activators {
                event(AliceEvent.START)
            }

            action {
                reactions.alice?.say(
                    text = "Привет. Пока здесь мало функций, но давай попробуем что-нибудь сделать",
                    tts = "Привет. Что будем делать?"
                )
                reactions.buttons("Создать заявку", "Найти отдел")
            }
        }

        state("createCall") {
            activators {
                regex("создать заявку")
            }

            action {
                reactions.run {
                    say("Расскажите о вашей проблеме. Начните с фразы \"У меня проблема\"")
                    go("acceptCall")
                }
            }
        }

        state("acceptCall") {
            activators {
                regex(".*проблема.*")
            }
            action {
                val description = request.input.replace("у меня проблема", "")
                //val responseCall = createCall(description)
                val responseCall = "Operation completed successfull"
                if (responseCall == "Operation completed successfull") {
                    reactions.alice?.say(
                        "Заявка зарегистрирована. ${request.alice?.request?.command}. В ближайшее время с вами свяжутся",
                        "Ваша заявка успешно создана"
                    )

                } else {
                    reactions.alice?.say("Попробуйте еще раз", "Что-то пошло не так...")
                    reactions.go("main")
                }
            }
        }


        state("findOU") {
            activators {
                regex("найти отдел")
            }

            action {
                reactions.say("Скажите название отдела.")

            }

            state("find") {
                activators {
                    regex(".*отдел.*")
                    //regex(".*")
                }

                action {
                    reactions.run {
                        val title = request.input.replace("отдел", "")
                        val ou : List<OU.OuInfo> =
                            findOU(title)
                        when {
                            ou.isEmpty() -> {
                                alice?.say("Не найдено. Проверьте название", "Ничего не нашла")
                            }
                            ou.size == 1 -> {
                                alice?.say("Название отдела: ${ou[0].title}", "Отдел ${ou[0].title}")
                            }
                            else -> {
                                alice?.say("Найдены отделы")
                                val titleOus : Array<Button> = (ou.map {Button(it.title, hide = false)}).toTypedArray()
                                alice?.buttons(*titleOus)
                            }
                        }
//                    sayRandom(
//                        "Ваш донос зарегистрирован под номером ${random(1000, 9000)}.",
//                        "Оставайтесь на месте. Не трогайте вещественные доказательства."
//                    )
//                    say("У вас есть еще какая-нибудь информация?")
//                    buttons("Да", "Нет")
                    }
                }


            }
        }

        state("no") {
            activators {
                regex("нет")
                regex("Нет")
                regex("отбой")
                regex("спасибо")
            }

            action {
                reactions.sayRandom("Ну ок.", "Ладно, пока")
                reactions.alice?.endSession()
            }
        }



        state("fallback", noContext = true) {
            activators {
                catchAll()
            }

            action {
                reactions.alice?.say("Попробуйте еще раз сначала", "Что-то пошло не так...")
                reactions.alice?.endSession()
            }
        }
    }
}

private fun createCall(input: String): Any {
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
        "$SERVER_URL/services/rest/create/${fqn}/${attr}?${ACCESS_KEY}"

    if (LOG) println(newUrl)

    val responseString = requestToServer(newUrl)

    return responseString.substring(0, if (responseString.length < 100) responseString.length - 1 else 100)

}

private fun findOU(text: String?): List<OU.OuInfo> {
    val findOuUrl =
        "$SERVER_URL/services/rest/search/ou/%7Btitle:\"${text?.capitalize()}\"%7D/?$ACCESS_KEY"
    var responseString = requestToServer(findOuUrl)
    val listOus = responseString.replace("[", "").replace("]", "").split(",")
    val ous = mutableListOf<OU.OuInfo>()
    for (ou in listOus) {
        val getOuUrl =
            "$SERVER_URL/services/rest/get/${ou.replace(
                "\"",
                ""
            )}?$ACCESS_KEY"
        responseString = requestToServer(getOuUrl)
        ous.add(Gson().fromJson(responseString, OU.OuInfo::class.java))
    }
    return ous
}


fun requestToServer(url: String): String {
    var response = ""
    runBlocking {
        val client = HttpClient()
        // Get the content of an URL.
        response = client.get<String>(url)
        // Once the previous request is done, get the content of an URL.
        client.close()
    }
    return response
}