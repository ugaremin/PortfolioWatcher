package com.example.portfoliowatcher.ui.stocks

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.portfoliowatcher.data.StocksData
import com.example.portfoliowatcher.data.StocksDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class StocksViewModel : ViewModel() {

    val stocksLiveData = MutableLiveData<List<StocksData>>()

    fun uploadStocksData() {
        GlobalScope.launch(Dispatchers.IO) {
            val dataSource = StocksDataSource()
            val stocks = dataSource.getStocksData()
            stocksLiveData.postValue(stocks)
        }
    }
}