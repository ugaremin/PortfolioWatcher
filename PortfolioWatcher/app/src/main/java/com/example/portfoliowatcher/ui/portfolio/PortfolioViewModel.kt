package com.example.portfoliowatcher.ui.portfolio

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.portfoliowatcher.data.StocksData
import com.example.portfoliowatcher.data.StocksDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PortfolioViewModel : ViewModel() {

    val stocksLiveData = MutableLiveData<List<StocksData>>()

    fun getAllStocks(context: Context) {
        GlobalScope.launch(Dispatchers.IO) {
            val dataSource = StocksDataSource()
            val stocks = dataSource.getPortfolioData(context)
            stocksLiveData.postValue(stocks)
        }
    }
}