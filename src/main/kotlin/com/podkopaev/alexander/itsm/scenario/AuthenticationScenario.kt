package com.podkopaev.alexander.itsm.scenario

import com.justai.jaicf.channel.telegram.telegram
import com.justai.jaicf.channel.yandexalice.alice
import com.justai.jaicf.channel.yandexalice.api.alice
import com.justai.jaicf.model.scenario.Scenario
import com.podkopaev.alexander.itsm.globalitsm.model.ItsmEmployee
import com.podkopaev.alexander.itsm.scenario.MainScenario.LOG
import com.podkopaev.alexander.itsm.scenario.MainScenario.server
import com.podkopaev.alexander.itsm.scenario.MainScenario.userAccessKey
import com.podkopaev.alexander.itsm.scenario.MainScenario.userName
import com.podkopaev.alexander.itsm.scenario.MainScenario.userUUID

object AuthenticationScenario : Scenario() {
    const val state = "/authentication"
    var userSD: ItsmEmployee.ItsmEmployeeInfo? = null


    init {

        state(state) {
            action {
                val userId = request.alice?.session?.userId
                //val message = request.telegram?.message
                //userSD = server.getUserByTelegramID(message?.from?.id)
                userSD = server.getUserByAliceId(userId)
                if (userSD != null) {
                    /* if (LOG) println("find user ${message?.from?.id}")
                     if (LOG) println("chat ID ${message}")
                     if (LOG) println("chat ID ${message?.chat}")*/

                    userUUID = userSD?.UUID.toString()
                    userName = userSD?.title.toString()
                    userAccessKey = userSD?.accessKey.toString()
                    reactions.alice?.say("Здравствуйте, ${userName},")
                    reactions.go("/main/start/")
                } else {
                    /* if (LOG) println("User don't find by telegramID ${message?.from?.id}")

                     if (LOG) println("chat ID ${message}")
                     if (LOG) println("chat ID ${message?.chat}")*/
//                    reactions.go("/getUserByPhone")
                    reactions.go("/findUserToAuth")

                }
            }
        }

/*        state("/getUserByPhone") {
            action {

                reactions.telegram?.say(
                    "Для аутентификации нам нужен ваш номер телефона",
                    replyMarkup = KeyboardReplyMarkup(
                        listOf(
                            listOf(
                                KeyboardButton("Разрешить телефон", true),
                                KeyboardButton("Не разрешать")
                            )
                        )
                    )
                )
//                    // Telegram incoming message
//                    val message = request.telegram?.message
//
//                    // Fetch username
//                    val username = message?.chat?.username
            }

            state("/noPhone") {
                activators {
                    regex("Не разрешать")
                }

                action {
                    reactions.say("Тогда не могу помочь")
                    reactions.go("/main/no")
                }
            }

            state("/getPhone") {
                activators {
                    event(TelegramEvent.CONTACT)
                }
                action {
                    val message = request.telegram?.message
                    //reactions.say(message.toString())
                    //reactions.say(message?.contact.toString())
                    userSD = server.getUserByPhone(message?.contact?.phoneNumber)
                    if (userSD != null) {
                        if (LOG) println("find user by phone $userSD")
                        reactions.telegram?.say(
                            "Вас зовут ${userSD?.title}?",
                            replyMarkup = KeyboardReplyMarkup.createSimpleKeyboard(
                                listOf(
                                    listOf("Да", "Нет")
                                ),
                                resizeKeyboard = true,
                                oneTimeKeyboard = true,
                                selective = true
                            )

                        )
                    } else {
                        if (LOG) println("User don't find by phone")
                        reactions.say("Обратитесь в тех. поддержку для актуализации номера телефона")
                        reactions.go("/main/no")
                    }
                }

                state("acceptUser") {
                    activators {
                        regex("да")
                    }
                    action {
                        if (LOG) println("user $userSD need update")
                        val message = request.telegram?.message
                        val response = server.setTelegramIdForUser(userSD, message?.from?.id)
                        if (response == "Operation completed successfull") {
                            if (LOG) println("user $userSD update")

                            reactions.telegram?.say(
                                "Учетная запись обновлена"
                            )
                            userUUID = userSD?.UUID.toString()
                            userName = userSD?.title.toString()
                            userAccessKey = userSD?.accessKey.toString()
                            reactions.go("/main/start/")

                        } else {
                            reactions.say("Учетная запись не обновлена. Для настройки аутентификации свяжитесь с технической поддежркой")
                            reactions.go("/goodbye")
                        }
                    }
                }


                state("declineUser") {
                    activators {
                        regex("нет")
                    }
                    action {
                        reactions.say("Обратитесь в тех. поддержку для актуализации номера телефона")
                        reactions.go("/goodbye")
                    }
                }


            }
        }*/


        state("/findUserToAuth") {
            action {
                reactions.say("Для аутентификации, скажите ваш ID в системе")
            }

            state("/checkUserId") {
               // activators {
                   // regex(".*")
                    catchAll()
                }
                action {

                    val message = request.alice?.request?.command
                    if (LOG) println("message $message")

                    //reactions.say(message.toString())
                    //reactions.say(message?.contact.toString())
                    userSD = server.getUserBySdId(message)
                    if (userSD != null) {
                        if (LOG) println("find user by id $userSD")
                        reactions.alice?.say("Вас зовут ${userSD?.title}?")

                    } else {
                        if (LOG) println("User don't find by id")
                        reactions.say("Обратитесь в тех поддержку")
                        reactions.go("/main/no")
                    }
                }

                state("acceptUser") {
                    activators {
                        regex("да")
                    }
                    action {
                        if (LOG) println("user $userSD need update")
                        val message = request.alice?.session?.userId
                        val response = server.setAliceIdForUser(userSD,message)
                        if (response == "Operation completed successfull") {
                            if (LOG) println("user $userSD update")

                            reactions.telegram?.say(
                                "Учетная запись обновлена"
                            )
                            userUUID = userSD?.UUID.toString()
                            userName = userSD?.title.toString()
                            userAccessKey = userSD?.accessKey.toString()
                            reactions.go("/main/start/")

                        } else {
                            reactions.say("Учетная запись не обновлена. Для настройки аутентификации свяжитесь с технической поддежркой")
                            reactions.go("/goodbye")
                        }
                    }
                }

                state("declineUser") {
                    activators {
                        regex("нет")
                    }
                    action {
                        reactions.say("Свяжитесь с тех. поддержкой")
                        reactions.go("/goodbye")
                    }
                }
            }

            state("repeat") {
                activators {
                    regex("еще")
                }
                action {
                    reactions.go(AuthenticationScenario.state)
                }
            }
        }


    }
}

