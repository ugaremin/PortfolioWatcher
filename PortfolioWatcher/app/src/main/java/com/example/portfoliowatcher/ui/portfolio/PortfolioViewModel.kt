package com.example.portfoliowatcher.ui.portfolio

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.portfoliowatcher.data.StocksData
import com.example.portfoliowatcher.data.StocksDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PortfolioViewModel : ViewModel() {

    val stocksLiveData = MutableLiveData<List<StocksData>>()

    fun getAllStocks() {
        GlobalScope.launch(Dispatchers.IO) {
            val dataSource = StocksDataSource()
            val stocks = dataSource.getStocksData()
            stocksLiveData.postValue(stocks)
        }
    }
}