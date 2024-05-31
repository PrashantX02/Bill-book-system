package com.example.billbook

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.billbook.Utils.FirestoreHelper
import com.example.billbook.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.UUID

class Signup : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var FirestoreHelper: FirebaseFirestore
    private lateinit var imageUri: Uri
    private var imageUrl: String? = null

    private val PICK_IMAGE_REQUEST = 1011

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val rootView = binding.root
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        firebaseAuth = FirebaseAuth.getInstance()
        FirestoreHelper = FirebaseFirestore.getInstance()

        binding.textView.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
        binding.userImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        binding.button.setOnClickListener {
            val name = binding.nameEt.text.toString()
            val email = binding.emailEt.text.toString()
            val phone = binding.numberEt.text.toString()
            val address = binding.pinEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty() && address.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {
                    create_user(email, pass, name, phone, address)
                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data!!

            Picasso.get().load(imageUri.toString()).into(binding.userImage)
        }
    }

    fun create_user(email: String, pass: String, name: String, phone: String, address: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Get the user ID of the newly created user
                    val userId = firebaseAuth.currentUser?.uid

                    if (userId != null) {
                        val storageReference = FirebaseStorage.getInstance().reference
                        val ref = storageReference.child("profile_pic").child(userId)

                        ref.putFile(imageUri)
                            .addOnSuccessListener {
                                // Get the download URL for the uploaded image
                                ref.downloadUrl.addOnSuccessListener { uri ->
                                    val imageUrl = uri.toString()

                                    // Create a user map to store in Firestore
                                    val user = hashMapOf(
                                        "name" to name,
                                        "email" to email,
                                        "phone" to phone,
                                        "address" to address,
                                        "img" to imageUrl
                                    )

                                    // Add the user to Firestore under the "users" collection
                                    FirestoreHelper.collection("Shops").document("Users")
                                        .collection("User Data").document(userId).set(user)
                                        .addOnSuccessListener {
                                            val intent = Intent(this, MainActivity::class.java)
                                            startActivity(intent)
                                            Toast.makeText(
                                                this,
                                                "User Sign Up Successful",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(
                                                this,
                                                "Error saving user: ${e.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }.addOnFailureListener { e ->
                                    Toast.makeText(
                                        this,
                                        "Error getting download URL: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }.addOnFailureListener { e ->
                                Toast.makeText(
                                    this,
                                    "Error uploading image: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        Toast.makeText(this, "User ID is null", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Error creating user: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }

    }
}

