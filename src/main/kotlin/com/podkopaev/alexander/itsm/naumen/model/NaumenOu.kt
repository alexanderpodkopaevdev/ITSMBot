package com.podkopaev.alexander.itsm.naumen.model

import com.podkopaev.alexander.itsm.globalitsm.model.ItsmOu

class NaumenOU : ItsmOu() {
    data class NaumenOuInfo(
        val KEsInUse: List<Any>,
        val UUID: String,
        val adress: Any,
        val author: Any,
        val creationDate: String,
        val employees: List<Any>,
        val folders: List<Any>,
        val head: Any,
        val icon: Icon,
        val idHolder: Any,
        val lastModifiedDate: String,
        val metaClass: String,
        val number: Any,
        val ouSecGroups: List<Any>,
        val parent: Any,
        val recipientAgreements: List<RecipientAgreement>,
        val removalDate: Any,
        val removed: Boolean,
        val serviceCalls: List<Any>,
        val system_icon: Any
    ) : ItsmOuInfo() {
        init {
            super.title = metaClass
        }
    }

    data class Icon(
        val UUID: String,
        val code: String,
        val metaClass: String,
        val title: String
    )

    data class RecipientAgreement(
        val UUID: String,
        val metaClass: String,
        val title: String
    )
}

