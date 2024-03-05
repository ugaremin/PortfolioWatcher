package com.ugaremin.portfoliowatcher.ui.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ugaremin.portfoliowatcher.R


class StockDetailDialogFragment : BottomSheetDialogFragment() {

    private val viewModel: StockDetailDialogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_stock_detail_dialog, container, false)
        val textView1 = view?.findViewById<TextView>(R.id.textView1)
        val textView2 = view?.findViewById<TextView>(R.id.textView2)
        val textView3 = view?.findViewById<TextView>(R.id.textView3)
        viewModel.bottomSheetItems.observe(viewLifecycleOwner) { items ->
            textView1?.text = items[0].text
            textView2?.text = items[1].text
            textView3?.text = items[2].text
        }
        return view
    }

}