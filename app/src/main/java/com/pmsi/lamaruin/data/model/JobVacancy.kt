package com.pmsi.lamaruin.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class JobVacancy constructor(
    var id_job: String? = "",
    var job_name: String? = "",
    var job_desc: String? = "",
    var job_category: String? = "",
    var qualification: String? = "",
    var id_recruiter: String? ="",
    var job_deadline: Long? = null

)
