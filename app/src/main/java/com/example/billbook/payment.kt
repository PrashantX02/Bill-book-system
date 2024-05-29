package com.example.billbook

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.LottieAnimationView
import com.example.billbook.Fragments.Shopinfo
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class payment : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment)

        val done: LottieAnimationView = findViewById(R.id.done)
        val wait: LottieAnimationView = findViewById(R.id.wait)

        wait.visibility = View.GONE
        done.visibility = View.GONE

        val name = intent.getStringExtra("name")
        val price = intent.getStringExtra("price")
        val degree = intent.getStringExtra("degree")

        val data = Transaction_data(name.toString(), "+ " + price.toString())

        val icon: ImageView = findViewById(R.id.payment_icon)

        val n: TextView = findViewById(R.id.cname)
        n.text = name

        if (degree != null) {
            if (degree.toInt() == 1) icon.setImageResource(R.drawable.gpay)
            else if (degree.toInt() == 2) icon.setImageResource(R.drawable.ppe)
            else icon.setImageResource(R.drawable.bpe)
            //done.playAnimation()
        }

        val pay: Button = findViewById(R.id.button2)
        pay.text = price

        pay.setOnClickListener {

            wait.visibility = View.VISIBLE
            wait.playAnimation()
            FirebaseDatabase.getInstance().getReference("transaction").push().setValue(data)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        wait.visibility = View.GONE
                        done.visibility = View.VISIBLE
                        done.playAnimation()
                        done.postDelayed({
                            done.visibility = View.GONE
                        }, 2000)
                    } else {
                        wait.visibility = View.GONE
                        done.visibility = View.GONE
                        Snackbar.make(
                            this.findViewById(android.R.id.content),
                            "transaction unsuccessful",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

}