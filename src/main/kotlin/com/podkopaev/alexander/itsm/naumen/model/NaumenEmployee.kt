package com.podkopaev.alexander.itsm.naumen.model

import com.podkopaev.alexander.itsm.globalitsm.model.ItsmEmployee

class NaumenEmployee : ItsmEmployee() {
    data class NaumenEmployeeInfo(
        val KEsInUse: List<Any>,
        val all_Group: List<Any>,
        val approvalsMaden: List<Any>,
        val author: Any,
        val cityPhoneNumber: Any,
        val commentAuthorAlias: Any,
        val createdChReqs: List<Any>,
        val createdPrblms: List<Any>,
        val createdSCs: List<Any>,
        val createdTasks: List<Any>,
        val creationDate: String,
        val cs: String,
        val dateOfBirth: Any,
        val email: Any,
        val employeeForIntegration: Boolean,
        val employeeSecGroups: List<Any>,
        val firstName: String,
        val folders: List<Any>,
        val homePhoneNumber: Any,
        val icon: Any,
        val idHolder: Any,
        val image: Any,
        val immediateSupervisor: Any,
        val internalPhoneNumber: Any,
        val intro: String,
        val isEmployeeActive: Boolean,
        val isEmployeeLocked: Boolean,
        val isGenPass: Boolean,
        val keyEmployee: Boolean,
        val lastModifiedDate: String,
        val lastName: String,
        val license: List<String>,
        val login: Any,
        val metaClass: String,
        val middleName: String,
//        val mobilePhoneNumber: String,
        val negotiation: List<Any>,
        val number: Any,
        val parent: NaumenOU.NaumenOuInfo,
        val password: Any,
        val performer: Boolean,
        val phonesIndex: String,
        val post: String,
        val privateCode: Any,
        val processDocumen: String,
        val quickStart: String,
        val recipientAgreements: Any,
        val removalDate: Any,
        val removed: Boolean,
        val respForChReqs: List<Any>,
        val respForPrblms: List<Any>,
        val respForSCs: List<Any>,
        val respForTasks: Any,
        val serviceCalls: List<NaumenCall.NaumenServiceCall>,
        val shortDescr1: Any,
        val shortDescr2: String,
        val shortDescr3: Any,
        val subscription: List<Any>,
        val system_icon: Any,
        val teams: List<Any>,
//        val telegram: Any,
        val tet: Any,
//        val title: String,
        val votes: List<Any>,
        val workMng: String,
        val workRecords: List<Any>,
        val workReports: List<Any>
    ) : ItsmEmployeeInfo()
}