package com.podkopaev.alexander.itsm.scenario

import com.justai.jaicf.channel.telegram.telegram
import com.justai.jaicf.channel.yandexalice.alice
import com.justai.jaicf.channel.yandexalice.api.model.Button
import com.justai.jaicf.model.scenario.Scenario
import com.podkopaev.alexander.itsm.globalitsm.model.ItsmKb

object FindKnowledgeBase : Scenario() {

    const val state = "/findingArticle"

    init {
        state(state) {
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
                        articles = MainScenario.server.findArticle(title)
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
                                telegram?.say("Найдены статьи", articles.map { it.title })
                            }
                        }
                    }
                }

                state("readArticle") {
                    activators {
                        regex("да")
                    }
                    action {
                        reactions.say(articles[0].text)
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
    }
}