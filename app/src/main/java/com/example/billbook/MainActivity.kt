package com.example.billbook

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.billbook.Fragments.Dashboard
import com.example.billbook.Fragments.Invoice
import com.example.billbook.Fragments.Shopinfo
import com.example.billbook.Model.ProductData
import com.example.billbook.Utils.FirestoreHelper
import com.example.billbook.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.QuerySnapshot

class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var productList: ArrayList<ProductData>
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        setFragment(Dashboard())

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.dashboard -> setFragment(Dashboard())
                R.id.invoice -> setFragment(Invoice())
                R.id.Shop_info -> setFragment(Shopinfo())
                else -> return@setOnItemSelectedListener false
            }
            return@setOnItemSelectedListener true
        }

        FirebaseAnalytics.getInstance(this)

        binding.favNewInv.setOnClickListener {
            val intent = Intent(this, NewInvoice::class.java)
            startActivity(intent)
        }
        getProduct()
        binding.addProductNew.setOnClickListener {
            val intent = Intent(this, AddProductNew::class.java)
            startActivity(intent)
        }
        binding.receivePayment.setOnClickListener {
            val intent = Intent(this, RecivedPayment::class.java)
            startActivity(intent)
        }
    }

    private fun setFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_Frame, fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()
    }

    fun getProduct() {
        productList = ArrayList()
        FirestoreHelper.fireDatabase.collection("Shops").document("Shop Name").collection("Product")
            .get().addOnCompleteListener(object : OnCompleteListener<QuerySnapshot> {
                override fun onComplete(p0: Task<QuerySnapshot>) {
                    if (p0.isSuccessful) {
                        for (i in p0.result) {
                            val pName = i.get("Product Name").toString()
                            val pMRP = i.get("Product MRP").toString()
                            val pPrice = i.get("Product Price").toString()
                            val pDiscount = "0"
                            val pStock = i.get("Product Stock").toString()
                            productList.add(
                                ProductData(
                                    productName = pName,
                                    productMrp = pMRP,
                                    sellingPrice = pPrice,
                                    productDiscount = pDiscount,
                                    productStock = pStock
                                )
                            )
                            Log.d("ListItems", productList.toString())
                        }
                    }
                }
            })
    }
}
