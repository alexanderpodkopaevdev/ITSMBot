package com.podkopaev.alexander.itsm.scenario

import com.justai.jaicf.channel.yandexalice.AliceEvent
import com.justai.jaicf.channel.yandexalice.alice
import com.justai.jaicf.channel.yandexalice.api.alice
import com.justai.jaicf.channel.yandexalice.api.model.Button
import com.justai.jaicf.model.scenario.Scenario
import com.podkopaev.alexander.itsm.globalitsm.ItsmServer
import com.podkopaev.alexander.itsm.naumen.NaumenServer
import com.podkopaev.alexander.itsm.globalitsm.model.ItsmKb

val LOG: Boolean = false

object MainScenario : Scenario() {
    init {
        val server: ItsmServer = NaumenServer()
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
//                        val responseCall = "Operation completed successfull"

                        val responseCall = server.createCall(description)
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
                        val ou = server.findOU(title)
                        when {
                            ou.isEmpty() -> {
                                alice?.say("Не найдено. Проверьте название", "Ничего не нашла")
                            }
                            ou.size == 1 -> {
                                alice?.say("Название отдела: ${ou[0].ouTitle}", "Отдел ${ou[0].ouTitle}")
                            }
                            else -> {
                                alice?.say("Найдены отделы")
                                val titleOus: Array<Button> =
                                    (ou.map { Button(it.ouTitle, hide = false) }).toTypedArray()
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
                lateinit var article: List<ItsmKb.ItsmKbInfo>

                action {
                    reactions.run {
                        val title = request.input.replace("как", "").replace("?", "")
                        article = server.findArticle(title)
                        when {
                            article.isEmpty() -> {
                                alice?.say("Не найдено. Попробуйте переформулировать", "Ничего не нашла")
                            }
                            article.size == 1 -> {
                                alice?.say(
                                    "Название статьи: ${article[0].kbTitle}. Читать?",
                                    "Статья ${article[0].kbTitle}. Будем читать?"
                                )
                                buttons("Да", "Нет")
                            }
                            else -> {
                                alice?.say("Найдено несколько статей. Какую выбрать?")
                                val titleOus: Array<Button> =
                                    (article.map { Button(it.kbTitle, hide = false) }).toTypedArray()
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
                        reactions.say("${article[0].kbText}")
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






