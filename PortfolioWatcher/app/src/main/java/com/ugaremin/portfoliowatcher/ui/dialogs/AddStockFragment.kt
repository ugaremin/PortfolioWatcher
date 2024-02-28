package com.ugaremin.portfoliowatcher.ui.dialogs

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ugaremin.portfoliowatcher.data.Room.AppDatabase
import com.ugaremin.portfoliowatcher.R
import com.ugaremin.portfoliowatcher.data.Room.Stocks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


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

        dialogStockCost?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    val cost = it.toString()
                    if (cost.isNotEmpty() && dialogStockAmount?.text.toString() != "") {
                        val costFloat = cost.toFloatOrNull()
                        val amountString: String = dialogStockAmount?.text.toString()
                        val amountFloat = amountString.toFloatOrNull()
                        val result = amountFloat?.let { it1 -> costFloat?.times(it1) }
                        if (result != null) {
                            dialogStockTotal?.text = result.toString()
                        }

                        addButton?.isEnabled=true

                    } else {
                        dialogStockTotal?.text = "0"
                        addButton?.isEnabled=false
                    }
                }
            }

        })

        dialogStockAmount?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    val amount = it.toString()
                    if (amount.isNotEmpty()) {
                        val amountFloat = amount.toFloatOrNull()
                        val costString: String = dialogStockCost?.text.toString()
                        val costFloat = costString.toFloatOrNull()
                        val result = costFloat?.let { it1 -> amountFloat?.times(it1) }
                        if (result != null) {
                            dialogStockTotal?.text = result.toString()
                        }
                        addButton?.isEnabled=true
                    } else {
                        dialogStockTotal?.text = "0"
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

        addButton?.setOnClickListener(View.OnClickListener {

            try {
                val saveStockName = dialogStockName?.text.toString()
                val saveStockAmount = dialogStockAmount?.text.toString().toInt()
                val saveStockTotal = dialogStockTotal?.text.toString().toDouble()
                val stock = Stocks(0, saveStockName, saveStockAmount ,saveStockTotal)
                persistStock(stock)
            }catch (e: NumberFormatException) {
                Toast.makeText(requireContext(), getString(R.string.add_stock_error_message), Toast.LENGTH_SHORT).show()

            }
            dialog.dismiss()
        })




        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun persistStock(stocks: Stocks){
        GlobalScope.launch {
            launch(Dispatchers.IO){
                val userDao = AppDatabase.getInstance(requireContext()).stocksDao()
                if(userDao.findStock(stocks.stock_name) == null){
                    userDao.insert(stocks)
                }else{
                    launch (Dispatchers.Main){
                        Toast.makeText(requireContext(), getString(R.string.already_exist), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }


}