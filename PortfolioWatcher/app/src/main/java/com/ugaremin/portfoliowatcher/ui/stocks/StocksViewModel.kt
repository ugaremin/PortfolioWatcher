package com.ugaremin.portfoliowatcher.ui.stocks

import android.os.Handler
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ugaremin.portfoliowatcher.data.StockDetailData
import com.ugaremin.portfoliowatcher.data.StocksData
import com.ugaremin.portfoliowatcher.data.StocksDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class StocksViewModel : ViewModel() {

    val stocksLiveData = MutableLiveData<List<StocksData>>()

    private val handler = Handler()
    private val interval = 15000L // 10 saniye

    private val databaseRequestRunnable = object : Runnable {
        override fun run() {
            uploadStocksData()
            handler.postDelayed(this, interval)
        }
    }

    fun startDatabaseRequest() {
        handler.post(databaseRequestRunnable)
    }

    fun stopDatabaseRequest() {
        handler.removeCallbacks(databaseRequestRunnable)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(null)
    }

    fun uploadStocksData() {
        viewModelScope.launch(Dispatchers.IO) {
            val dataSource = StocksDataSource()
            val stocks = dataSource.getStocksData()
            stocksLiveData.postValue(stocks)
        }
    }

    fun uploadStockDetail(stockUrl: String, stockName: String, completion: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dataSource = StocksDataSource()
                dataSource.getStocksDetail(stockUrl, stockName)
                completion(true)
            } catch (e: Exception) {
                completion(false)
            }
        }
    }
}