package com.podkopaev.alexander.itsm.globalitsm

import com.podkopaev.alexander.itsm.globalitsm.model.ItsmCall
import com.podkopaev.alexander.itsm.globalitsm.model.ItsmEmployee
import com.podkopaev.alexander.itsm.globalitsm.model.ItsmKb
import com.podkopaev.alexander.itsm.globalitsm.model.ItsmOu
import com.podkopaev.alexander.itsm.naumen.model.NaumenEmployee

interface ItsmServer {
    fun createCall(input: String?): Any
    fun findOU(text: String?): List<ItsmOu.ItsmOuInfo>
    fun findArticle(text: String?): List<ItsmKb.ItsmKbInfo>
    fun findCall(text: String?): List<ItsmCall.ItsmServiceCall>
    fun getUserByTelegramID(telegramId: Long?) : ItsmEmployee.ItsmEmployeeInfo?
    fun setTelegramIdForUser(sdUser: ItsmEmployee.ItsmEmployeeInfo?, telegramId: Long?): String
    fun getUserByPhone(phoneNumber: String?): ItsmEmployee.ItsmEmployeeInfo?

}