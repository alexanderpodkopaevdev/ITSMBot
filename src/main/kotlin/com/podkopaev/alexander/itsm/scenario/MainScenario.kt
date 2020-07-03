package com.podkopaev.alexander.itsm.scenario

import com.justai.jaicf.channel.telegram.telegram
import com.justai.jaicf.channel.yandexalice.AliceEvent
import com.justai.jaicf.channel.yandexalice.alice
import com.justai.jaicf.channel.yandexalice.api.alice
import com.justai.jaicf.channel.yandexalice.api.model.Button
import com.justai.jaicf.model.scenario.Scenario
import com.podkopaev.alexander.itsm.globalitsm.ItsmServer
import com.podkopaev.alexander.itsm.naumen.NaumenServer
import com.podkopaev.alexander.itsm.globalitsm.model.ItsmKb
import me.ivmg.telegram.entities.KeyboardButton
import me.ivmg.telegram.entities.KeyboardReplyMarkup
import me.ivmg.telegram.entities.ReplyKeyboardRemove

val LOG: Boolean = true

object MainScenario : Scenario() {
    init {
        val server: ItsmServer = NaumenServer()
        state("main") {
            activators {
                event(AliceEvent.START)
                regex("/start")
            }

            action {
                reactions.alice?.sayRandom("Привет.", "Здравствуйте.", "Добрый день.")
                reactions.telegram?.sayRandom("Привет.", "Здравствуйте.", "Добрый день.")
                reactions.go("/main/start")
            }

            state("start") {
                activators {
                    //   event(AliceEvent.START)
                }

                action {
                    reactions.alice?.say(
                        text = "Что делаем?",
                        tts = "Чем займемся?"
                    )
                    reactions.buttons("Создать заявку", "Найти отдел", "Открыть базу знаний", "Проверить статус")

                    reactions.telegram?.say(
                        "Что делаем?",
//                        listOf("Создать заявку", "Найти отдел", "Открыть базу знаний", "Проверить статус")
                        replyMarkup = KeyboardReplyMarkup.createSimpleKeyboard(
                            listOf(listOf("Создать заявку", "Найти отдел"), listOf("Открыть базу знаний", "Проверить статус")),
                            resizeKeyboard = true,
                            oneTimeKeyboard = true,
                            selective = true
                        )
                    )
                }

                state("createCall") {
                    activators {
                        regex("создать заявку")
                    }

                    action {
                        reactions.say("Расскажите о вашей проблеме")
                        reactions.telegram?.say("",replyMarkup = ReplyKeyboardRemove())

                    }

                    state("acceptCall") {
                        lateinit var description: String
                        activators {
                            catchAll()
                        }
                        action {
                            description = request.input
//                                (request.alice?.request?.command ?: request.telegram?.message?.text).toString()
                            reactions.alice?.say(
                                "Проблема: ${request.alice?.request?.command}. Регистрируем заявку?",
                                "Всё верно? Регистрируем заявку?"
                            )
                            reactions.buttons("Да", "Нет")

                            reactions.telegram?.say(
                                "Проблема: ${request.telegram?.message?.text}. Регистрируем заявку?",
                                listOf("Да", "Нет")
                            )
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

                                    reactions.telegram?.say(
                                        "Заявка зарегистрирована. В ближайшее время с вами свяжутся \n" +
                                                "Хотите сделать что-то еще?",
                                        listOf("Да", "Нет")
                                    )
                                } else {
                                    reactions.say("Что-то пошло не так... Попробуйте еще раз")
                                    reactions.go("/main/start/createCall")
                                }
                            }

                            state("continue") {
                                activators {
                                    regex("да")
                                }

                                action {
                                    reactions.say("Отлично")
                                    reactions.go("/main/start")
                                }
                            }

                            state("notCreatingCall") {
                                activators {
                                    regex("нет")
                                }

                                action {
                                    reactions.go("/goodbye")
                                }
                            }
                        }

                        state("notCreatingCall") {
                            activators {
                                regex("нет")
                            }

                            action {
                                reactions.say("Хорошо, заявку не делаем\n")
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
                                        telegram?.say("Не найдено. Попробуйте снова")
                                        go("/main/start")
                                    }
                                    ou.size == 1 -> {
                                        say("Отдел ${ou[0].title}")
                                        reactions.alice?.say(
                                            "\nХотите сделать что-то еще?"
                                        )
                                        reactions.buttons("Да", "Нет")

                                        reactions.telegram?.say(
                                            "\nХотите сделать что-то еще?",
                                            listOf("Да", "Нет")
                                        )
                                    }
                                    else -> {
                                        alice?.say("Найдены отделы")
                                        if (LOG) println(ou[0].title)
                                        val titleOus: Array<Button> =
                                            (ou.map { Button(it.title, hide = false) }).toTypedArray()
                                        alice?.buttons(*titleOus)
                                        telegram?.say("Найдены отделы", ou.map { it.title })
                                    }
                                }
                            }
                        }

                        state("continue") {
                            activators {
                                regex("да")
                            }

                            action {
                                reactions.say("Отлично\n")
                                reactions.go("/main/start")
                            }
                        }


                        state("notCreatingCall") {
                            activators {
                                regex("нет")
                            }

                            action {
                                reactions.go("/goodbye")
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
                            regex(".*")
                        }
                        lateinit var articles: List<ItsmKb.ItsmKbInfo>
                        action {
                            reactions.run {
                                val title = request.input.replace("как ", "").replace("?", "")
                                articles = server.findArticle(title)
                                when {
                                    articles.isEmpty() -> {
                                        alice?.say("Не найдено. Попробуйте переформулировать", "Ничего не нашла")
                                        telegram?.say("Не найдено. Попробуйте снова")
                                        go("/main/start")
                                    }
                                    articles.size == 1 -> {
                                        alice?.say(
                                            "Название статьи: ${articles[0].title}. Читать?",
                                            "Статья ${articles[0].title}. Будем читать?"
                                        )
                                        buttons("Да", "Нет")
                                        telegram?.say(
                                            "Статья ${articles[0].title}. Будем читать?",
                                            listOf("Да", "Нет")
                                        )
                                    }
                                    else -> {
                                        say("Найдено несколько статей. Какую выбрать?")
                                        val titleArticles: Array<Button> =
                                            (articles.map { Button(it.title, hide = false) }).toTypedArray()
                                        alice?.buttons(*titleArticles)
                                        telegram?.say("Найдены отделы", articles.map { it.title })
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

                            state("continue") {
                                activators {
                                    regex("да")
                                }

                                action {
                                    reactions.say("Отлично\n")
                                    reactions.go("/main/start")
                                }
                            }


                            state("notCreatingCall") {
                                activators {
                                    regex("нет")
                                }

                                action {
                                    reactions.go("/goodbye")
                                }
                            }
                        }

                        state("notRead") {
                            activators {
                                regex("нет")
                            }

                            action {
                                reactions.say("Хорошо. \n")
                                reactions.go("/main/start")
                            }
                        }


                    }
                }

                state("checkState") {
                    activators {
                        regex("проверить статус")
                    }

                    action {
                        reactions.say("Скажите номер заявки.")
                    }

                    state("findCall") {
                        activators {
                            regex(".*")
                        }
                        action {
                            reactions.run {
                                val title = request.input.replace("номер", "")
                                val calls = server.findCall(title)
                                if (LOG) println(calls.toString())
                                when {
                                    calls.isEmpty() -> {
                                        say("Не найдено. Проверьте номер")
                                        go("/main/start")
                                    }
                                    calls.size == 1 -> {
                                        say("Найдена заявка ${calls[0].title}. Статус заявки: ${calls[0].state}")
                                        reactions.alice?.say(
                                            "\nХотите сделать что-то еще?"
                                        )
                                        reactions.buttons("Да", "Нет")

                                        reactions.telegram?.say(
                                            "\nХотите сделать что-то еще?",
                                            listOf("Да", "Нет")
                                        )
                                    }
                                    else -> {
                                        alice?.say("Найдены заявки")
                                        if (LOG) println(calls[0].title)
                                        val numberCalls: Array<Button> =
                                            (calls.map { Button(it.title, hide = false) }).toTypedArray()
                                        alice?.buttons(*numberCalls)
                                        telegram?.say("Найдены заявки", calls.map { it.title })
                                    }
                                }
                            }
                        }
                        state("continue") {
                            activators {
                                regex("да")
                            }

                            action {
                                reactions.say("Отлично\n")
                                reactions.go("/main/start")
                            }
                        }


                        state("notCreatingCall") {
                            activators {
                                regex("нет")
                            }

                            action {
                                reactions.go("/goodbye")
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
                regex("отмена")
            }

            action {
                reactions.sayRandom("Ну ок.", "Ладно, пока")
                reactions.alice?.endSession()

            }
        }

        state("goodbye") {
            activators {
                regex("закройся")
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

                reactions.telegram?.say("Что-то пошло не так... Попробуйте еще раз сначала")
            }
        }
    }
}






