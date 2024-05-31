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
import androidx.recyclerview.widget.RecyclerView
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.QuerySnapshot


class NewInvoice : AppCompatActivity(), (ProductData) -> Unit {
    lateinit var binding: ActivityNewInvoiceBinding
    lateinit var productList: ArrayList<ProductData>
    var pName = ""

    private  var paymentDegree = 0
    private var strobe = 0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewInvoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarNewInvoice)

        // visibility of recent recycler View
        binding.purchasedItemsRV.visibility = View.GONE
        binding.viewRecent.setOnClickListener{
            if(strobe == 0 ) {
                binding.purchasedItemsRV.visibility = View.VISIBLE
                strobe = 1;
            }else{
                binding.purchasedItemsRV.visibility = View.GONE
                strobe = 0;
            }
        }// end of recent recycler view

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


        // Setting up recent adapter

        var recentAdapter : RecyclerView = findViewById(R.id.purchasedItemsRV)

        FirebaseDatabase.getInstance().getReference("transaction").addValueEventListener(object  : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                var list = mutableListOf<Transaction_data>()
                for(snap in snapshot.children.toList().asReversed()){
                    val name = snap.child("name").getValue(String::class.java).toString()
                    val price = snap.child("price").getValue(String::class.java).toString()
                    val product = snap.child("product").getValue(String::class.java).toString()
                    list.add(Transaction_data(name,price,product))
                    if(list.size == 5) break
                }

                val adapter = TransectionAdapter(list,1)
                recentAdapter.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        //  end up setting of recent recyclerView

        binding.addInvoice.setOnClickListener {
            val name = binding.customerName.text?.trim().toString()
            val price = binding.overallTv.text.toString()
            val product = binding.productEditText.text.trim().toString()
            val Cname = binding.customerName.text.toString()
            if( !name.isEmpty() && !price.isEmpty() && !product.isEmpty() && !Cname.isEmpty() ){
                uploadInvoice(Cname)
                // sending data to payment gateway
                val intent = Intent(this, payment::class.java)
                intent.putExtra("name", name)
                intent.putExtra("price", price)
                intent.putExtra("product", product)
                intent.putExtra("degree", paymentDegree.toString())

                startActivity(intent)
            }else{
                Snackbar.make(binding.root,"Required Field Is Missing",Snackbar.LENGTH_SHORT).show()
            }
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
        binding.productEditText.setText(p1.productName)
        binding.discountPercent.text = p1.productDiscount
        binding.productStock.text = p1.productStock

        val price  = binding.sellingPrice.text.toString().toInt()
        val discount = binding.discountPercent.text.toString().toInt()

        val discount_ammount = price * (discount/100.0)
        val price_after_discount = price - discount_ammount
        binding.overallTv.text = "₹ $price_after_discount"
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
                            val pDiscount = i.get("Product Discount").toString()
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
