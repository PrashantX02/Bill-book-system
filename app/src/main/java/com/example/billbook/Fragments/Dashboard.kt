package com.example.billbook.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.billbook.R
import com.example.billbook.Transaction_data
import com.example.billbook.TransectionAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.w3c.dom.ls.LSInput
import java.util.Stack

class Dashboard : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_dashboard, container, false)

        val transaction: RecyclerView = view.findViewById(R.id.TranSectionAdapter)

        transaction.layoutManager = LinearLayoutManager(context)


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
        return view
    }

}
