package com.example.portfoliowatcher.ui.dialogs

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.portfoliowatcher.R
import java.time.temporal.TemporalAmount


class AddStockFragment : Fragment() {

    private var builder: AlertDialog.Builder? = null
    private lateinit var stockName: String
    private lateinit var stockValue: String

    private var dialogStockName: TextView? = null
    private var dialogStockCost: EditText? = null
    private var dialogStockAmount: EditText? = null
    private var dialogStockTotal: TextView? = null
    private var addButton: Button? = null
    private var cancelButton: Button? = null




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_stock, container, false)

        val itemName = arguments?.getString("itemName")
        val itemValue = arguments?.getString("itemValue")

        stockName = itemName.toString()
        stockValue = itemValue.toString().replace(',', '.')

        builder = AlertDialog.Builder(context)
        showWarningAlertDialog(stockName, stockValue)

        return view
    }

    fun showWarningAlertDialog(stockName: String, stockValue: String) {
        val inflater = layoutInflater
        val alertLayout: View = inflater.inflate(R.layout.add_stock_dialog, null)
        builder?.setView(alertLayout)
        builder?.setCancelable(true)
        val dialog = builder!!.create()
        dialogStockName = alertLayout.findViewById(R.id.stockNameTextView)
        dialogStockCost = alertLayout.findViewById(R.id.stockCostEditText)
        dialogStockAmount = alertLayout.findViewById(R.id.stockAmauntEditText)
        dialogStockTotal = alertLayout.findViewById(R.id.stockTotalTextView)
        addButton = alertLayout.findViewById(R.id.addButton)
        cancelButton = alertLayout.findViewById(R.id.cancelButton)

        dialogStockAmount?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    val input = it.toString()
                    if (input.isNotEmpty()) {
                        val number = input.toFloatOrNull()
                        val result = number?.times(stockValue.toFloatOrNull()!!)
                        dialogStockTotal?.text = result.toString()
                        addButton?.isEnabled=true
                    } else {
                        dialogStockTotal?.text = ""
                        addButton?.isEnabled=false
                    }
                }
            }
        })

        if(dialogStockName != null){
            dialogStockName!!.text = stockName
        }

        if(dialogStockCost != null){
            dialogStockCost!!.text = Editable.Factory.getInstance().newEditable(stockValue)
        }

        cancelButton?.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })


        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }


}