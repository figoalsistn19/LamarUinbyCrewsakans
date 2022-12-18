package com.pmsi.lamaruin.data.model

data class CreateRecruiter constructor(
    var name: String?= "",
    var email: String = "",
    var foto: String = "",
    var no_hp: String = "",
    var id_recruiter: String = "",
    var password_recruiter: String = "",
    var company_name: String = "",
    var company_profile: String = "",
    var company_address: String = ""
)