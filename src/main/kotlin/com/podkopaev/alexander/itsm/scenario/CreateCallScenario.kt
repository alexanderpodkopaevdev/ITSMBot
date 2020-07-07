package com.podkopaev.alexander.itsm.scenario

import com.justai.jaicf.channel.telegram.telegram
import com.justai.jaicf.channel.yandexalice.alice
import com.justai.jaicf.channel.yandexalice.api.alice
import com.justai.jaicf.model.scenario.Scenario
import com.podkopaev.alexander.itsm.scenario.MainScenario.server
import me.ivmg.telegram.entities.ReplyKeyboardRemove

object CreateCallScenario : Scenario() {

    const val state = "/creatingCall"

    init {

        state(state) {
//            activators {
//                regex("создать заявку")
//            }

            action {
                reactions.say("Расскажите о вашей проблеме")
                reactions.telegram?.say("", replyMarkup = ReplyKeyboardRemove())

            }

            state("acceptCall") {
                lateinit var description: String
                activators {
                    catchAll()
                }
                action {
                    description = request.input
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
    }
}