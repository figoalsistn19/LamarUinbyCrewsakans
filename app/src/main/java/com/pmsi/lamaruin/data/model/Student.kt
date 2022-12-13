package com.pmsi.lamaruin.data.model

data class CreateStudent constructor(
    var username_student: String = "",
    var name: String?= "",
    var email: String = "",
    var foto: String = "",
    var description : String = "",
    var address : String = "",
    var phone : String = "",
    var interest : String = "",
    var skill : String = "",
    var nama_cv : String = "",
    var url_cv : String = "",
    var id_cv : String = "",
    var id_student: String = "",
    var password_student: String = "",
    var add_profile: Boolean = false
)