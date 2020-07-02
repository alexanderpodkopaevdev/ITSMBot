package com.podkopaev.alexander.itsm.scenario

import com.justai.jaicf.channel.yandexalice.AliceEvent
import com.justai.jaicf.channel.yandexalice.alice
import com.justai.jaicf.channel.yandexalice.api.alice
import com.justai.jaicf.channel.yandexalice.api.model.Button
import com.justai.jaicf.channel.yandexalice.api.model.Image
import com.justai.jaicf.channel.yandexalice.api.model.ItemsList
import com.justai.jaicf.model.scenario.Scenario
import com.podkopaev.alexander.itsm.globalitsm.ItsmServer
import com.podkopaev.alexander.itsm.naumen.NaumenServer
import com.podkopaev.alexander.itsm.globalitsm.model.ItsmKb

val LOG: Boolean = true

object MainScenario : Scenario() {
    init {
        val server: ItsmServer = NaumenServer()
        state("main") {
            activators {
                event(AliceEvent.START)
            }

            action {
                reactions.alice?.sayRandom("Привет.", "Здравствуйте.", "Добрый день.")
                reactions.go("/main/start")
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

                state("createCall") {
                    activators {
                        regex("создать заявку")
                    }

                    action {
                        reactions.run {
                            say("Расскажите о вашей проблеме")
                        }
                    }

                    state("acceptCall") {
                        lateinit var description: String
                        activators {
                            catchAll()
                        }
                        action {
                            description = request.alice?.request?.command.toString()
                            reactions.say(context.dialogContext.backStateStack.toString())

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
                        val responseCall = "Operation completed successfull"

//                                val responseCall = server.createCall(description)
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
                                    reactions.go("/main/start/createCall")
                                }
                            }

                            state("continue") {
                                activators {
                                    regex("да")
                                }

                                action {
                                    reactions.alice?.say("Отлично", "Отлично")
                                    reactions.go("/main/start")
                                }
                            }
                        }

                        state("notCreatingCall") {
                            activators {
                                regex("нет")
                            }

                            action {
                                reactions.alice?.say("Хорошо, заявку не делаем\n", "Хорошо. Не создаем")
                                reactions.go("/main/start")
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
                            //regex(".*отдел.*")
                            regex(".*")
                        }

                        action {
                            reactions.run {
                                val title = request.input.replace("отдел", "")
                                val ou = server.findOU(title)
                                if (LOG) println(ou.toString())
                                when {
                                    ou.isEmpty() -> {
                                        alice?.say("Не найдено. Проверьте название", "Ничего не нашла")
                                    }
                                    ou.size == 1 -> {
                                        alice?.say("Название отдела: ${ou[0].title}", "Отдел ${ou[0].title}")
                                    }
                                    else -> {
                                        alice?.say("Найдены отделы")
                                        //https://lh3.googleusercontent.com/proxy/A1Mm4C6TzlzTGigZOnPsq3uQ8UHTq6djlgio8grjgORtMCO2MoBfQ_xMqvYxeXtPPaMGdGwGM5WhITpC
                                        if (LOG) println(ou[0].title)
                                        val titleOus: Array<Button> =
                                            (ou.map { Button(it.title, hide = false) }).toTypedArray()
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
                        lateinit var articles: List<ItsmKb.ItsmKbInfo>

                        action {
                            reactions.run {

                                val title = request.input.replace("как", "").replace("?", "")
                                articles = server.findArticle(title)
                                when {
                                    articles.isEmpty() -> {
                                        alice?.say("Не найдено. Попробуйте переформулировать", "Ничего не нашла")
                                    }
                                    articles.size == 1 -> {
                                        alice?.say(
                                            "Название статьи: ${articles[0].title}. Читать?",
                                            "Статья ${articles[0].title}. Будем читать?"
                                        )
                                        buttons("Да", "Нет")
                                    }
                                    else -> {
                                        alice?.say("Найдено несколько статей. Какую выбрать?")/*
                                val itemsArticle: MutableList<Image> = mutableListOf()
                                for (article in articles) {
                                    itemsArticle.add(
                                        Image(
                                            alice?.api?.getImageId("https://c7.hotpng.com/preview/967/314/354/knowledge-sharing-share-icon-knowledge-base-computer-icons-knowledge-vector.jpg").toString(),
                                            article.title
                                        )
                                    )
                                }
                                alice?.itemsList(header = "Статьи")
                                    ?.items?.addAll(itemsArticle)*/
                                        val titleOus: Array<Button> =
                                            (articles.map { Button(it.title, hide = false) }).toTypedArray()
                                        alice?.buttons(*titleOus)
//                                go("/knowledgeBase/findArticle")
                                    }
                                }
                            }
                        }

                        state("readArticle") {
                            activators {
                                regex("да")
                            }
                            action {
                                reactions.say("${articles[0].text}")
                                reactions.say("\nЧто-нибудь еще?")
                            }

                            state("readArticle") {
                                activators {
                                    regex("да")
                                }
                                action {
                                    reactions.go("/main/start")
                                }
                            }
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
                regex("выйти")
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






