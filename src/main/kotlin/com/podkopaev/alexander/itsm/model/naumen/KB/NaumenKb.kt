package com.podkopaev.alexander.itsm.model.naumen.KB

import com.podkopaev.alexander.itsm.model.naumen.ItsmKb

class NaumenKB : ItsmKb() {
    data class KbInfo(
        val UUID: String,
        val author: Any,
        val avMark: Any,
        val basedOnReq: Any,
        val creationDate: String,
        val folders: List<Any>,
        val icon: Any,
        val lastModifiedDate: String,
        val metaClass: String,
        val myMarkNew: Any,
        val number: Any,
        val parent: Parent,
        val problems: List<Any>,
        val removalDate: Any,
        val removed: Boolean,
        val responsible: Any,
        val responsibleEmployee: Any,
        val responsibleStartTime: Any,
        val responsibleTeam: Any,
        val services: List<Any>,
        val system_icon: Any,
        val text: String,
        val title: String,
        val yourMark: Any
    ) : ItsmKbInfo(text, title)

    data class Parent(
        val UUID: String,
        val metaClass: String,
        val title: String
    )
}