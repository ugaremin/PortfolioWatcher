package com.ugaremin.portfoliowatcher.ui.portfolio

import android.content.Context
import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ugaremin.portfoliowatcher.data.StockDetailData
import com.ugaremin.portfoliowatcher.data.StocksData
import com.ugaremin.portfoliowatcher.data.StocksDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PortfolioViewModel : ViewModel() {

    val stocksLiveData = MutableLiveData<MutableList<StocksData>>()
    val stocksDetailLiveData = MutableLiveData<List<StockDetailData>>()

    private val handler = Handler()
    private val interval = 15000L

    private val databaseRequestRunnable = object : Runnable {
        override fun run() {
            _contextLiveData.value?.let { context ->

                getAllStocks(context)
            }

            handler.postDelayed(this, interval)
        }
    }

    private val _contextLiveData = MutableLiveData<Context>()
    val contextLiveData: LiveData<Context>
        get() = _contextLiveData

    fun setContext(context: Context) {
        _contextLiveData.value = context
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

    fun getAllStocks(context: Context) {
        GlobalScope.launch(Dispatchers.IO) {
            val dataSource = StocksDataSource()
            val stocks = dataSource.getPortfolioData(context)
            stocksLiveData.postValue(stocks.toMutableList())
        }
    }

    fun uploadStocksDetail(stockUrl: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val dataSource = StocksDataSource()
            val stocks = dataSource.getStocksDetail(stockUrl)
            stocksDetailLiveData.postValue(stocks)
        }
    }
}