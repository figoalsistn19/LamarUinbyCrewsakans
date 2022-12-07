package com.pmsi.lamaruin.data.model

data class CreateRecruiter constructor(
    var username_recruiter: String = "",
    var name: String?= "",
    var email: String = "",
    var no_hp: String = "",
    var id_recruiter: String = "",
    var password_student: String = ""
)