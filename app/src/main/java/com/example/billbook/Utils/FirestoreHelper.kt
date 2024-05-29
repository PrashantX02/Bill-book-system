package com.example.billbook.Utils

import com.google.firebase.firestore.FirebaseFirestore

class FirestoreHelper {
    companion object {
        val fireDatabase = FirebaseFirestore.getInstance()
    }
}