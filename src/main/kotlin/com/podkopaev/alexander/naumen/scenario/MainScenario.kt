package com.podkopaev.alexander.naumen.scenario

import com.google.gson.Gson
import com.justai.jaicf.channel.yandexalice.AliceEvent
import com.justai.jaicf.channel.yandexalice.alice
import com.justai.jaicf.channel.yandexalice.api.alice
import com.justai.jaicf.channel.yandexalice.api.model.Button
import com.justai.jaicf.model.scenario.Scenario
import com.podkopaev.alexander.naumen.Data.ACCESS_KEY
import com.podkopaev.alexander.naumen.Data.SERVER_URL
import com.podkopaev.alexander.naumen.model.KB.KB
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
                reactions.alice?.sayRandom("Привет.", "Здравствуйте.", "Добрый день.")
                reactions.go("/start")
            }
        }

        state("start") {
            activators {
                event(AliceEvent.START)
            }

            action {
                reactions.alice?.say(
                    text = "Что делаем?",
                    tts = "Чем займемся?"
                )
                reactions.buttons("Создать заявку", "Найти отдел", "Открыть базу знаний")
            }
        }

        state("createCall") {
            activators {
                regex("создать заявку")
            }

            action {
                reactions.run {
                    say("Расскажите о вашей проблеме. Начните с фразы \"У меня проблема\"")
                }
            }

            state("acceptCall") {
                lateinit var description: String
                activators {
                    catchAll()
                }
                action {
                    description = request.alice?.request?.command.toString()
                    reactions.alice?.say(
                        "Проблема: ${request.alice?.request?.command}. Регистрируем заявку?",
                        "Всё верно? Регистрируем заявку?"
                    )
                    reactions.buttons("Да", "Нет")

                }

                state("creatingCall") {
                    activators {
                        regex("да")
                    }

                    action {
//                        Заглушка создания заявки
                    val responseCall = createCall(description)
//                        val responseCall = "Operation completed successfull"
                        if (responseCall == "Operation completed successfull") {
                            reactions.alice?.say(
                                "Заявка зарегистрирована. В ближайшее время с вами свяжутся",
                                "Ваша заявка успешно создана"
                            )
                            reactions.alice?.say(
                                "Хотите сделать что-то еще?"
                            )
                            reactions.buttons("Да", "Нет")

                        } else {
                            reactions.alice?.say("Попробуйте еще раз", "Что-то пошло не так...")
                            reactions.go("/main/createCall")
                        }
                    }

                    state("continue") {
                        activators {
                            regex("да")
                        }

                        action {
                            reactions.alice?.say("Отлично", "Отлично")
                            reactions.go("/start")
                        }
                    }
                }

                state("notCreatingCall") {
                    activators {
                        regex("нет")
                    }

                    action {
                        reactions.alice?.say("Хорошо, заявку не делаем", "Хорошо. Не создаем")
                        reactions.go("/start")
                    }
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
                        val ou: List<OU.OuInfo> =
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
                                val titleOus: Array<Button> = (ou.map { Button(it.title, hide = false) }).toTypedArray()
                                alice?.buttons(*titleOus)
                            }
                        }
                    }
                }
            }
        }

        state("knowledgeBase") {
            activators {
                regex(".*база знаний.*")
                regex(".*базу знаний.*")
            }
            action {
                reactions.say("Какую статью будем искать?")
            }
            state("findArticle") {
                activators {
                    catchAll()
                }
                lateinit var article: List<KB.KbInfo>

                action {
                    reactions.run {
                        val title = request.input.replace("как", "").replace("?","")
                        article = findArticle(title)
//
//                                https://softline-presale.itsm365.com/sd/services/rest/search/KB$KBArticle/%7Btitle:%D0%B7%D0%B0%D1%8F%D0%B2%D0%BA%D0%B0%7D?accessKey=589caa54-2528-45c3-b16c-86be9f2081c5
//                                https://softline-presale.itsm365.com/sd/services/rest/search/KB$KBArticle/%7Btext:%D1%8D%D1%82%D0%BE%D0%B9%7D?accessKey=589caa54-2528-45c3-b16c-86be9f2081c5
//
//                        https://softline-presale.itsm365.com/sd/services/rest/get/KB$2409401?accessKey=589caa54-2528-45c3-b16c-86be9f2081c5
                        when {
                            article.isEmpty() -> {
                                alice?.say("Не найдено. Попробуйте переформулировать", "Ничего не нашла")
                            }
                            article.size == 1 -> {
                                alice?.say(
                                    "Название статьи: ${article[0].title}. Читать?",
                                    "Статья ${article[0].title}. Будем читать?"
                                )
                                buttons("Да", "Нет")
                            }
                            else -> {
                                alice?.say("Найдено несколько статей. Какую выбрать?")
                                val titleOus: Array<Button> =
                                    (article.map { Button(it.title, hide = false) }).toTypedArray()
                                alice?.buttons(*titleOus)
                            }
                        }
                    }
                }

                state("readArticle") {
                    activators {
                        regex("да")
                    }
                    action {
                        reactions.say("${article[0].text}")
                        reactions.say("\nЧто-нибудь еще?")
                    }

                    state("readArticle") {
                        activators {
                            regex("да")
                        }
                        action {
                            reactions.go("/start")
                        }
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

private fun createCall(input: String?): Any {
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
        if (ou.isNullOrEmpty()) break
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

private fun findArticle(text: String?): List<KB.KbInfo> {
    val findArticleUrl =
        "$SERVER_URL/services/rest/search/KB\$KBArticle/%7Btitle:\"${text}\"%7D/?$ACCESS_KEY"
    var responseString = requestToServer(findArticleUrl)
    val listArticles = responseString.replace("[", "").replace("]", "").split(",")
    val articles = mutableListOf<KB.KbInfo>()
    for (article in listArticles) {
        if (article.isNullOrEmpty()) break
        val getArticleUrl =
            "$SERVER_URL/services/rest/get/${article.replace(
                "\"",
                ""
            )}?$ACCESS_KEY"
        responseString = requestToServer(getArticleUrl)
        articles.add(Gson().fromJson(responseString, KB.KbInfo::class.java))
    }
    return articles
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