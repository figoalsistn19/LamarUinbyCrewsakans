package com.pmsi.lamaruin.data.model

data class JobVacancy constructor(
    var id_job: String = "",
    var company_name: String?= "",
    var company_desc: String = "",
    var job_desc: String = "",
    var qualification: String = "",
    var job_field: String = ""
)
