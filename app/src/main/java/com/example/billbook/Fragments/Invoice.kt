package com.example.billbook.Fragments

import android.animation.LayoutTransition
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.billbook.Adapters.InvoicesAdapter
import com.example.billbook.R
import com.example.billbook.databinding.FragmentInvoiceBinding


class Invoice : Fragment() {
    val list = ArrayList<String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_invoice, container, false)
        val binding = FragmentInvoiceBinding.bind(view)
        list.add("Customer 1")
        list.add("Customer 2")
        list.add("Customer 3")
        list.add("Customer 4")
        list.add("Customer 5")
        binding.todayRv.setHasFixedSize(true)
        binding.todayRv.layoutManager = LinearLayoutManager(requireContext())
        binding.todayRv.adapter = InvoicesAdapter(requireContext(), list)


        binding.allInvRv.setHasFixedSize(true)
        binding.allInvRv.layoutManager = LinearLayoutManager(requireContext())
        binding.allInvRv.adapter = InvoicesAdapter(requireContext(), list)


        // Drop Down Visiblity


        binding.dropDownArrow2.setOnClickListener(View.OnClickListener {
            visiblityToggle(
                binding.todayRv, binding.dropDownArrow4, binding.todayInvParent, binding.parentINV
            )
        })
        binding.dropDownArrow.setOnClickListener(View.OnClickListener {
            visiblityToggle(
                binding.allInvRv, binding.dropDownArrow3, binding.allInvParent, binding.parentINV
            )
        })
        return view
    }

    private fun visiblityToggle(
        view: View, image: ImageView, parent: LinearLayout, parentMain: LinearLayout
    ) {
        var layoutTransition: LayoutTransition
        var layoutTransitionParent: LayoutTransition
        layoutTransition = LayoutTransition()
        layoutTransitionParent = LayoutTransition()
        if (view.isVisible) {
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            layoutTransitionParent.enableTransitionType(LayoutTransition.CHANGING)
            parentMain.layoutTransition = layoutTransitionParent
            parent.layoutTransition = layoutTransition
            view.visibility = View.GONE
            image.setImageResource(R.drawable.baseline_arrow_drop_up_54)
        } else {
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            layoutTransitionParent.enableTransitionType(LayoutTransition.CHANGING)
            parentMain.layoutTransition = layoutTransitionParent
            parent.layoutTransition = layoutTransition
            view.visibility = View.VISIBLE
            image.setImageResource(R.drawable.ic_arrow)
        }
    }
}