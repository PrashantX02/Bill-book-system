package com.example.billbook

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.billbook.Utils.FirestoreHelper
import com.example.billbook.databinding.ActivityAddProductNewBinding
import com.example.billbook.databinding.AddProductBinding
import com.google.android.material.snackbar.Snackbar

class AddProductNew : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductNewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductNewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Find the view with ID 'main'
        val mainView = findViewById<View>(R.id.main)

        binding.addProductSI.setOnClickListener {
            showAddDialog()
        }

        // Add null check for the view
        mainView?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        } ?: run {
            Log.e("AddProductNew", "View with ID 'main' not found")
        }
    }

    private fun showAddDialog() {
        val view =
            LayoutInflater.from(this).inflate(R.layout.add_product, binding.root, false)
        val dialogBinding = AddProductBinding.bind(view)
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(view)
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        dialogBinding.addProduct.setOnClickListener {

            addProduct(dialogBinding, dialog, view)
            dialog.dismiss() // Dismiss the dialog after adding the product
        }
    }


    fun addProduct(dialogBinding: AddProductBinding, dialog: Dialog, view: View) {
        val hashMap = hashMapOf<String, Any>(
            "Product Name" to dialogBinding.addProductName.text.toString(),
            "Product MRP" to dialogBinding.addProductMRP.text.toString(),
            "Product Price" to dialogBinding.addProductPrice.text.toString(),
            "Product Discount" to dialogBinding.addProductDiscount.text.toString(),
            "Product Stock" to dialogBinding.addProductStock.text.toString()
        )
        FirestoreHelper.fireDatabase.collection("Shops").document("Shop Name").collection("Product")
            .add(hashMap).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    dialog.dismiss()
                    Toast.makeText(this, "Product Added", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

