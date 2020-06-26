package com.podkopaev.alexander.naumen

import com.justai.jaicf.BotEngine
import com.justai.jaicf.activator.catchall.CatchAllActivator
import com.justai.jaicf.activator.event.BaseEventActivator
import com.justai.jaicf.activator.regex.RegexActivator
import com.podkopaev.alexander.naumen.scenario.MainScenario

val skill = BotEngine(
    model = MainScenario.model,
    activators = arrayOf(
        RegexActivator,
        BaseEventActivator,
        CatchAllActivator
    )
)