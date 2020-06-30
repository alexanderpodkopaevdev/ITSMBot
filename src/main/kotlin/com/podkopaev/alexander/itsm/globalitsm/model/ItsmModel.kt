package com.podkopaev.alexander.itsm.globalitsm.model

abstract class ItsmKb {
    abstract class ItsmKbInfo(
        var kbText: String,
        var kbTitle: String
    )
}

abstract class ItsmOu {
    abstract class ItsmOuInfo(
        var ouTitle: String
    )
}