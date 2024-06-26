package com.ugaremin.portfoliowatcher.ui.portfolio

import android.content.Context
import android.os.Handler
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ugaremin.portfoliowatcher.data.Room.AppDatabase
import com.ugaremin.portfoliowatcher.data.StocksData
import com.ugaremin.portfoliowatcher.data.StocksDataSource
import com.ugaremin.portfoliowatcher.data.TotalPortfolioStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PortfolioViewModel : ViewModel() {

    val TAG = PortfolioViewModel::class.simpleName
    val stocksLiveData = MutableLiveData<MutableList<StocksData>>()
    private val _updateLiveData = MutableLiveData<Unit>()

    val updateGeneralValuesLiveData: LiveData<Unit>
        get() = _updateLiveData

    private val handler = Handler()
    private val interval = 15000L

    private val databaseRequestRunnable = object : Runnable {
        override fun run() {
            _contextLiveData.value?.let { context ->

                getAllStocks(context)
                getSumLastValue(context){
                    Log.i(TAG, "stocks data updated")
                }
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
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dataSource = StocksDataSource()
                val stocks = dataSource.getStocksData(context, true)
                stocksLiveData.postValue(stocks.toMutableList())
            } catch (e: Exception) {
                Log.e(TAG, "Error: ${e.message}")
            }

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

    fun deleteStocks(context: Context, stockName: String, completion: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                AppDatabase.getInstance(context).stocksDao().deleteStockByStockName(stockName)
                getSumLastValue(context){
                    Log.i(TAG, "stocks removed")
                }
                completion()
            } catch (e: Exception) {
                Log.e(TAG, "Error: ${e.message}")
            }

        }

    }

    fun getSumStocks(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userDao = AppDatabase.getInstance(context).stocksDao()
                TotalPortfolioStatus.sumStocks = userDao.getTotal()
                TotalPortfolioStatus.sumStocks = userDao.getTotal()
            } catch (e: Exception) {
                Log.e(TAG, "Error: ${e.message}")
            }

        }
    }

    fun getSumLastValue(context: Context, completion: () -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userDao = AppDatabase.getInstance(context).stocksDao()
                TotalPortfolioStatus.sumStocks = userDao.getTotal()
                val dataSource = StocksDataSource()
                TotalPortfolioStatus.sumLastValues = dataSource.getTotalValueForPortfolio(context)
                TotalPortfolioStatus.totalProfit = TotalPortfolioStatus.sumLastValues - TotalPortfolioStatus.sumStocks
                TotalPortfolioStatus.totalProfitPercent = (TotalPortfolioStatus.totalProfit / TotalPortfolioStatus.sumStocks) * 100
                launch(Dispatchers.Main) {
                    _updateLiveData.value = Unit
                }
                completion()
            } catch (e: Exception) {
                Log.e(TAG, "Error: ${e.message}")
            }
        }
    }
}