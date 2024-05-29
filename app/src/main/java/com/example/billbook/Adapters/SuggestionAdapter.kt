package com.example.billbook.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.billbook.Model.ProductData
import com.example.billbook.databinding.SuggestionItemBinding

class SuggestionAdapter(
    val context: Context,
    var list: ArrayList<ProductData>,
    private val clickListener: (ProductData) -> Unit
) : RecyclerView.Adapter<SuggestionAdapter.holder>() {
    class holder(val binding: SuggestionItemBinding) : RecyclerView.ViewHolder(binding.root) {


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): holder {
        return SuggestionAdapter.holder(SuggestionItemBinding.inflate(LayoutInflater.from(context)))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: holder, position: Int) {
        holder.binding.suggProductName.text = list[position].productName
        holder.binding.suggPoductPrice.text = list[position].sellingPrice
        holder.binding.root.setOnClickListener {
            clickListener.invoke(list[position])
        }
    }
}