package com.ugaremin.portfoliowatcher.ui.dialogs

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.ugaremin.portfoliowatcher.R
import com.ugaremin.portfoliowatcher.data.Room.AppDatabase
import com.ugaremin.portfoliowatcher.data.Room.Stocks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddStockViewModel : ViewModel() {

    fun persistStock(context: Context,stocks: Stocks) {
        GlobalScope.launch {
            launch(Dispatchers.IO){
                val userDao = AppDatabase.getInstance(context).stocksDao()
                if(userDao.findStock(stocks.stock_name) == null){
                    userDao.insert(stocks)
                }else{
                    launch (Dispatchers.Main){
                        Toast.makeText(context, (R.string.already_exist), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

}
