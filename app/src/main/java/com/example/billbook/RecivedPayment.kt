package com.example.billbook

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.billbook.databinding.ActivityRecivedPaymentBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RecivedPayment : AppCompatActivity() {
    private lateinit var binding: ActivityRecivedPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecivedPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        // Find the view with ID 'main'
        val mainView = findViewById<View>(R.id.main)
        // Add null check for the view
        mainView?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        } ?: run {
            Log.e("RecivedPayment", "View with ID 'main' not found")
        }

        val transaction: RecyclerView = binding.root.findViewById(R.id.TranSectionAdapter)
        transaction.layoutManager = LinearLayoutManager(this)


        FirebaseDatabase.getInstance().getReference("transaction")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {


                    var list = mutableListOf<Transaction_data>()
                    for (snap in snapshot.children) {
                        val name = snap.child("name").getValue(String::class.java).toString()
                        val price = snap.child("price").getValue(String::class.java).toString()

                        list.add(Transaction_data(name, price))
                    }

                    val adapter = TransectionAdapter(list.reversed())
                    transaction.adapter = adapter
                }

                override fun onCancelled(error: DatabaseError) {
                    // nothing to do right now
                }
            })
    }

}
