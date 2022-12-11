package com.pmsi.lamaruin.data.model

data class CreateStudent constructor(
    var username_student: String = "",
    var name: String?= "",
    var email: String = "",
    var id_student: String = "",
    var password_student: String = "",
    var addProfile: Boolean = false
)