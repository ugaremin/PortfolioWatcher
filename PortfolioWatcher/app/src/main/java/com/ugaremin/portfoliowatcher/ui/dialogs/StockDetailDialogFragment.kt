package com.ugaremin.portfoliowatcher.ui.dialogs

import android.graphics.Color
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
        val stockNameTextView = view?.findViewById<TextView>(R.id.changesHeaderName)
        val weeklyChangeTextView = view?.findViewById<TextView>(R.id.weeklyTextView)
        val mounthlyChangeTextView = view?.findViewById<TextView>(R.id.mounthlyTextView)
        val yearlyChangeTextView = view?.findViewById<TextView>(R.id.yearlyTextView)
        viewModel.bottomSheetItems.observe(viewLifecycleOwner) { items ->
            stockNameTextView?.text = items[0].text + " " + (getString(R.string.stock_change_detail_header))
            weeklyChangeTextView?.text = items[1].text
            setTextViewColorBySecondCharacter(weeklyChangeTextView)
            mounthlyChangeTextView?.text = items[2].text
            setTextViewColorBySecondCharacter(mounthlyChangeTextView)
            yearlyChangeTextView?.text = items[3].text
            setTextViewColorBySecondCharacter(yearlyChangeTextView)
        }
        return view
    }

    fun setTextViewColorBySecondCharacter(textView: TextView?) {
        val text = textView?.text.toString()

        if (text == "%0,00") {
            textView?.setTextColor(Color.GRAY)
        } else if (text.length >= 2 && text[1] == '-') {
            textView?.setTextColor(Color.RED)
        } else {
            textView?.setTextColor(textView.context.getColor(R.color.green))
        }
    }

}