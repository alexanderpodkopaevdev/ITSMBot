package com.podkopaev.alexander.itsm.globalitsm

import com.podkopaev.alexander.itsm.globalitsm.model.ItsmKb
import com.podkopaev.alexander.itsm.globalitsm.model.ItsmOu

interface ItsmServer {
    fun createCall(input: String?): Any
    fun findOU(text: String?): List<ItsmOu.ItsmOuInfo>
    fun findArticle(text: String?): List<ItsmKb.ItsmKbInfo>
}