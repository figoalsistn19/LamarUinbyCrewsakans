package com.pmsi.lamaruin.data.model

data class AppliedJob constructor(
    var id_applied_job: String? = "",
    var status: String? = "",
    var id_student: String? = "",
    var id_job: String? = "",
    var company_photo: String? = "",
    var job_title: String? = "",
    var company_name: String? = ""
)