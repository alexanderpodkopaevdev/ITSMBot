package com.podkopaev.alexander.itsm.scenario

import com.justai.jaicf.channel.telegram.telegram
import com.justai.jaicf.channel.yandexalice.alice
import com.justai.jaicf.channel.yandexalice.api.model.Button
import com.justai.jaicf.model.scenario.Scenario
import com.podkopaev.alexander.itsm.scenario.MainScenario.LOG
import com.podkopaev.alexander.itsm.scenario.MainScenario.server

object FindOuScenario : Scenario() {

    const val state = "/findingOU"

    init {
        state(state) {
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
                                say(ou[0].title)

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
    }
}