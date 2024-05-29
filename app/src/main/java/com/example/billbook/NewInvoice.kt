package com.example.billbook

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.billbook.Adapters.ProductsAdapter
import com.example.billbook.Adapters.SuggestionAdapter
import com.example.billbook.Model.ProductModel
import com.example.billbook.Model.ProductData
import com.example.billbook.Utils.FirestoreHelper
import com.example.billbook.databinding.ActivityNewInvoiceBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.QuerySnapshot


class NewInvoice : AppCompatActivity(), (ProductData) -> Unit {
    lateinit var binding: ActivityNewInvoiceBinding
    lateinit var productList: ArrayList<ProductData>
    var pName = ""

    private  var paymentDegree = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewInvoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarNewInvoice)

        // selection of payment method

        binding.gpay.setOnClickListener{

            binding.ppe.setAlpha(1f)
            binding.gpay.setAlpha(1f)
            binding.bpe.setAlpha(1f)

            binding.ppe.setAlpha(0.3f)
            binding.bpe.setAlpha(0.3f)

            paymentDegree = 1
        }

        binding.ppe.setOnClickListener{

            binding.ppe.setAlpha(1f)
            binding.gpay.setAlpha(1f)
            binding.bpe.setAlpha(1f)

            binding.gpay.setAlpha(0.3f)
            binding.bpe.setAlpha(0.3f)

            paymentDegree = 2
        }

        binding.bpe.setOnClickListener{
            binding.ppe.setAlpha(1f)
            binding.gpay.setAlpha(1f)
            binding.bpe.setAlpha(1f)

            binding.ppe.setAlpha(0.3f)
            binding.gpay.setAlpha(0.3f)

            paymentDegree = 3
        }
        // end of selection

        binding.addInvoice.setOnClickListener {
            uploadInvoice(binding.customerName.text.toString())
            // sending data to payment gateway

            val name = binding.customerName.text.toString().trim()
            val price = binding.overallTv.text.toString()

            val intent = Intent(this,payment :: class.java)
            intent.putExtra("name",name)
            intent.putExtra("price",price)
            intent.putExtra("degree",paymentDegree.toString())

            startActivity(intent)
            // end of data addition
        }
        productList = ArrayList()
        binding.purchasedItemsRV.setHasFixedSize(true)
        binding.purchasedItemsRV.layoutManager = LinearLayoutManager(this)
        binding.addProduct.setOnClickListener {
            productList.add(
                ProductData(
                    pName,
                    binding.MRP.text.toString(),
                    binding.sellingPrice.text.toString(),
                    binding.discountPercent.text.toString(),
                    binding.productStock.text.toString()
                )
            )
            binding.purchasedItemsRV.adapter = ProductsAdapter(this, productList)
        }
        binding.suggestionRV.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.suggestionRV.setHasFixedSize(true)
        getProduct()
        binding.productEditText.doOnTextChanged { text, start, before, count ->
            if (start < count || start > count) {
                MainActivity.productList.forEach {
                    if (it.productName.lowercase().contains(text.toString().lowercase())) {
                        val tempList = ArrayList<ProductData>()
                        Log.d("Suggested Item", text.toString())
                        tempList.add(it)
                        val adapter = SuggestionAdapter(this, tempList, this)
                        binding.suggestionRV.adapter = adapter
                    }
                }
            }
        }
        // Set window insets listener for proper padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun uploadInvoice(custname: String) {
        FirestoreHelper.fireDatabase.collection("Shops")
            .document("Shop Name")
            .collection("Customers")
            .document(custname)
            .collection("Invoices")
            .document(System.currentTimeMillis().toString())
            .set(ProductModel(MainActivity.productList))
    }

    override fun invoke(p1: ProductData) {
        binding.sellingPrice.text = p1.sellingPrice
        binding.MRP.text = p1.productMrp
        pName = p1.productName
        // binding.productEditText.text = p1.productName.toString()
        binding.discountPercent.text = p1.productDiscount
        binding.productStock.text = p1.productStock
    }

    fun getProduct() {
        MainActivity.productList = ArrayList()
        FirestoreHelper.fireDatabase.collection("Shops")
            .document("Shop Name").collection("Product")
            .get()
            .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot> {
                override fun onComplete(p0: Task<QuerySnapshot>) {
                    if (p0.isSuccessful) {
                        for (i in p0.result) {
                            val pName = i.get("Product Name").toString()
                            val pMRP = i.get("Product MRP").toString()
                            val pPrice = i.get("Product Price").toString()
                            val pDiscount = "0"
                            val pStock = i.get("Product Stock").toString()
                            MainActivity.productList.add(
                                ProductData(
                                    productName = pName,
                                    productMrp = pMRP,
                                    sellingPrice = pPrice,
                                    productDiscount = pDiscount,
                                    productStock = pStock
                                )
                            )
                            Log.d("ListItems", MainActivity.productList.toString())
                        }
                    }
                }
            })
    }
}