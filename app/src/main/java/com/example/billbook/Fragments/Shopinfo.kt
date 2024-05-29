package com.example.billbook.Fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.billbook.R
import com.example.billbook.Utils.FirestoreHelper
import com.example.billbook.databinding.AddProductBinding
import com.example.billbook.databinding.FragmentShopinfoBinding
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import com.example.billbook.Login
import com.example.billbook.MainActivity

class Shopinfo : Fragment() {
    private var imageUri: Uri? = null


    private lateinit var binding: FragmentShopinfoBinding
    private lateinit var firefirebashAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentShopinfoBinding.inflate(inflater, container, false)

        firefirebashAuth = FirebaseAuth.getInstance()


        // Set onClickListener for signing out
        binding.signOutButton.setOnClickListener {
            signOut()
        }

        return binding.root
    }



    private fun signOut() {
        firefirebashAuth.signOut()
        val intent = Intent(requireActivity(), Login::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        requireActivity().finish()
    }
}
