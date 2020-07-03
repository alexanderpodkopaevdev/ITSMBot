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

object MainScenario :
    Scenario(dependencies = listOf(CreateCallScenario, FindOuScenario, FindKnowledgeBase, CheckCallState)) {

    val server: ItsmServer = NaumenServer()

    init {
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
                        replyMarkup = KeyboardReplyMarkup.createSimpleKeyboard(
                            listOf(
                                listOf("Создать заявку", "Найти отдел"),
                                listOf("Открыть базу знаний", "Проверить статус")
                            ),
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
                        reactions.go(CreateCallScenario.state)
                    }
                }

                state("findOU") {
                    activators {
                        regex("найти отдел")
                    }

                    action {
                        reactions.go(FindOuScenario.state)
                    }
                }

                state("findKnowledgeBase") {
                    activators {
                        regex(".*база знаний.*")
                        regex(".*базу знаний.*")
                    }
                    action {
                        reactions.go(FindKnowledgeBase.state)
                    }
                }

                state("checkState") {
                    activators {
                        regex("проверить статус")
                    }

                    action {
                        reactions.go(CheckCallState.state)
                    }
                }
            }
        }

        state("restart") {
            activators {
                regex("restart")
                regex("Restart")
                regex("заново")
            }
            action {
                reactions.go("/main")
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

                reactions.telegram?.say("Что-то пошло не так... Попробуйте еще раз сначала", listOf("Restart"))
            }
        }
    }
}






