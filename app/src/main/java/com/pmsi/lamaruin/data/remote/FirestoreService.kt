package com.pmsi.lamaruin.data.remote

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pmsi.lamaruin.data.model.CreateRecruiter
import com.pmsi.lamaruin.data.model.CreateStudent

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

    fun addStudents(student: CreateStudent) =
        db.collection("student")
            .document(student.id_student)
            .set(student)

    fun searchUsers(username: String, email: String) =
        db.collection("student")
            .whereEqualTo("username", username)
            .whereEqualTo("email", email)

    fun addRecruiter(recruiter: CreateRecruiter, listen: (String) -> Unit) =
        db.collection("recruiter")
            .add(recruiter)
            .onSuccessTask { doc ->
                doc.update("id_recruiter", doc.id)
                    .addOnSuccessListener {
                        listen(doc.id)
                    }
            }

    fun searchRecruiter(username: String, email: String) =
        db.collection("recruiter")
            .whereEqualTo("username", username)
            .whereEqualTo("email", email)
}