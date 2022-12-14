package com.pmsi.lamaruin.data.remote

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.*
import com.pmsi.lamaruin.data.model.*

class FirestoreService {
    private val db = Firebase.firestore

    fun addStudent(student: CreateStudent, listen: (String) -> Unit) =
        db.collection("student")
            .add(student)
            .onSuccessTask { doc ->
                doc.update("id_student", doc.id)
                    .addOnSuccessListener {
                        listen(doc.id)
                    }
            }

    fun addAppliedJob(appliedJob: AppliedJob, listen: (String) -> Unit) =
        db.collection("AppliedJob")
            .add(appliedJob)
            .onSuccessTask { doc ->
                doc.update("id_applied_job", doc.id)
                    .addOnSuccessListener {
                        listen(doc.id)
                    }
            }

    fun getSearchJob(text : String?) =
        db.collection("JobVacancy")
            .orderBy("job_name").startAt(text).endAt(text + "\uf8ff")

    fun hapusEdu (id_student: String, id_edu: String) =
        db.collection("student").document(id_student)
            .collection("education").document(id_edu)
            .delete()

    fun hapusJob (id_job: String) =
        db.collection("JobVacancy").document(id_job)
            .delete()

    fun hapusExp (id_student: String, id_exp: String) =
        db.collection("student").document(id_student)
            .collection("experience").document(id_exp)
            .delete()

    fun addPelamar(id_job: String, item_list_pelamar: ItemListPelamar) =
        db.collection("JobVacancy").document(id_job).collection("pelamar")
            .add(item_list_pelamar)

    fun checkPelamar(id_job: String, id_student: String) =
        db.collection("JobVacancy")
            .document(id_job)
            .collection("pelamar")
            .whereEqualTo("id_pelamar", id_student)

    fun getStatus(id_applicant: String) =
        db.collection("AppliedJob")
            .document(id_applicant)

    fun getPelamar(id_job: String) =
        db.collection("JobVacancy")
            .document(id_job)
            .collection("pelamar")
    fun addJob(jobVacancy: JobVacancy, listen: (String) -> Unit) =
        db.collection("JobVacancy")
            .add(jobVacancy)
            .onSuccessTask { doc ->
                doc.update("id_job", doc.id)
                    .addOnSuccessListener {
                        listen(doc.id)
                    }
            }

    fun loginStudent(email: String, password : String) =
        db.collection("student")
            .whereEqualTo("email", email)
            .whereEqualTo("password_student", password)

    fun loginRecruiter(email: String, password : String) =
        db.collection("recruiter")
            .whereEqualTo("email", email)
            .whereEqualTo("password_recruiter", password)

    fun addStudents(student: CreateStudent) =
        db.collection("student")
            .document(student.id_student)
            .set(student)

    fun searchUsers(email: String) =
        db.collection("student")
            .whereEqualTo("email", email)

    fun searchUsersById(id: String) =
        db.collection("student")
            .document(id)

    fun searchRecrById(id: String) =
        db.collection("recruiter")
            .document(id)

    fun addRecruiter(recruiter: CreateRecruiter, listen: (String) -> Unit) =
        db.collection("recruiter")
            .add(recruiter)
            .onSuccessTask { doc ->
                doc.update("id_recruiter", doc.id)
                    .addOnSuccessListener {
                        listen(doc.id)
                    }
            }

    fun searchRecruiter(email: String) =
        db.collection("recruiter")
            .whereEqualTo("email", email)

    fun searchKodeAkses(kode: String) =
        db.collection("kode")
            .whereEqualTo("kode", kode)
            .whereEqualTo("used", false)

    fun updateStatusKode(id_kode: String) =
        db.collection("kode").document(id_kode)
            .update("used", true)

    fun getEdu(id_user: String) : Query =
        db.collection("student")
            .document(id_user)
            .collection("education")

    fun getJob() : Query =
        db.collection("JobVacancy")

    fun getJobForRecruiter(id_student: String) : Query =
        db.collection("JobVacancy")
            .whereEqualTo("id_recruiter", id_student)

    fun getAppliedJob(id_student: String) =
        db.collection("AppliedJob")
            .whereEqualTo("id_student", id_student)

    fun updateApplicant(id: String) =
        db.collection("AppliedJob")
            .document(id)

    fun getJobById(id: String) =
        db.collection("JobVacancy")
            .document(id)

    fun getJobByRecruiterId(id_user: String) =
            db.collection("JobVacancy")
                    .whereEqualTo("id_recruiter", id_user)

    fun getJobWithInterest(interest: String) : Query =
        db.collection("JobVacancy")
//            .whereNotEqualTo("job_category", interest)

    fun getRecommendJob(interest: String) : Query =
        db.collection("JobVacancy")
            .whereEqualTo("job_category", interest)

    fun getJobByDate(date : Long) : Query =
        db.collection("JobVacancy")
            .whereGreaterThan("job_deadline", date)

    fun getRecruiterById(id_user: String) =
        db.collection("recruiter")
            .document(id_user)

    fun getExp(id_user: String) : Query =
        db.collection("student")
            .document(id_user)
            .collection("experience")

    fun addEducation(id_user: String, education: Education, listen: (String) -> Unit) =
        db.collection("student").document(id_user).collection("education")
            .add(education)
            .onSuccessTask { doc ->
                doc.update("id_edu", doc.id)
                    .addOnSuccessListener {
                        listen(doc.id)
                    }
            }

    fun addExperience(id_user: String, experience: Experience, listen: (String) -> Unit) =
        db.collection("student").document(id_user).collection("experience")
            .add(experience)
            .onSuccessTask { doc ->
                doc.update("id_experience", doc.id)
                    .addOnSuccessListener {
                        listen(doc.id)
                    }
            }
}