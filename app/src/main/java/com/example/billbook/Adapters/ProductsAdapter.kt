package com.example.billbook.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.billbook.Model.ProductData
import com.example.billbook.databinding.ActivityNewInvoiceBinding
import com.example.billbook.databinding.InvoiceItemsBinding

class ProductsAdapter(val context: Context, var list: ArrayList<ProductData>) :
    RecyclerView.Adapter<ProductsAdapter.holder>() {
    class holder(val binding: InvoiceItemsBinding) : RecyclerView.ViewHolder(binding.root) {


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): holder {
        return holder(InvoiceItemsBinding.inflate(LayoutInflater.from(context)))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: holder, position: Int) {
        holder.binding.custName.text = list[position].productName
    }
}