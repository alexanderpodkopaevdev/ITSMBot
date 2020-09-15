package com.podkopaev.alexander.itsm.scenario

import com.justai.jaicf.channel.telegram.contact
import com.justai.jaicf.channel.telegram.telegram
import com.justai.jaicf.channel.yandexalice.alice
import com.justai.jaicf.channel.yandexalice.api.model.Button
import com.justai.jaicf.model.scenario.Scenario
import com.podkopaev.alexander.itsm.scenario.MainScenario.LOG

object CheckCallState : Scenario() {
    const val state = "/checkingState"

    init {
        state(state) {
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
                        val calls = MainScenario.server.findCall(title)
                        if (LOG) println(calls.toString())
                        when {
                            calls.isEmpty() -> {
                                say("Не найдено. Проверьте номер")
                                go("/main/start")
                            }
                            calls.size == 1 -> {
                                val stateName = when (calls[0].state) {
                                    "registered" -> "Новая"
                                    "closed" -> "Закрыто"
                                    "inprogress" -> "В работе"
                                    "resolved" -> "Выполнена"
                                    else -> (calls[0].state)
                                }
                                say("Найдена заявка ${calls[0].shortDescr}. Статус заявки: $stateName")
                                reactions.alice?.say(
                                    "\nХотите сделать что-то еще?"
                                )
                                reactions.buttons("Да", "Нет")
                                reactions.telegram?.say(request.telegram?.contact.toString())

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