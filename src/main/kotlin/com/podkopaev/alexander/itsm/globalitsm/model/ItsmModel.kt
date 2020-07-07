package com.podkopaev.alexander.itsm.globalitsm.model

abstract class ItsmKb {
    abstract class ItsmKbInfo {
        var text: String = ""
        var title: String = ""
    }

}

abstract class ItsmOu {
    abstract class ItsmOuInfo {
        var title: String = ""
    }
}

abstract class ItsmCall {
    abstract class ItsmServiceCall {
        var title: String = ""
        var state: String = ""
    }
}

abstract class ItsmEmployee {
    abstract class ItsmEmployeeInfo {
        var UUID: String = ""
        var mobilePhoneNumber: String = ""
        var telegram: String = ""
        var title: String = ""
        var accessKey: String = ""

    }
}