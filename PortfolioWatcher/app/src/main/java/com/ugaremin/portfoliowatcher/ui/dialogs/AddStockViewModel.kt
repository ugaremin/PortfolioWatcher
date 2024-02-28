package com.ugaremin.portfoliowatcher.ui.dialogs

import android.content.Context
import androidx.lifecycle.ViewModel
import com.ugaremin.portfoliowatcher.data.Room.AppDatabase
import com.ugaremin.portfoliowatcher.data.Room.Stocks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddStockViewModel : ViewModel() {

    fun persistStock(context: Context,stocks: Stocks) : Boolean {
        var stockAlreadyExist = true
        GlobalScope.launch {
            launch(Dispatchers.IO){
                val userDao = AppDatabase.getInstance(context).stocksDao()
                if(userDao.findStock(stocks.stock_name) == null){
                    userDao.insert(stocks)
                    stockAlreadyExist = false;
                }else{
                    stockAlreadyExist = true
                }
            }
        }
        return stockAlreadyExist
    }

}
