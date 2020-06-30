package com.podkopaev.alexander.itsm

import com.podkopaev.alexander.itsm.model.naumen.ItsmKb
import com.podkopaev.alexander.itsm.model.naumen.ItsmOu
import com.podkopaev.alexander.itsm.model.naumen.KB.NaumenKB
import com.podkopaev.alexander.itsm.model.naumen.OU.NaumenOU

interface ItsmServer {
    fun createCall(input: String?): Any
    fun findOU(text: String?): List<ItsmOu.ItsmOuInfo>
    fun findArticle(text: String?): List<ItsmKb.ItsmKbInfo>
}