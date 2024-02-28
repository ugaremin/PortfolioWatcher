package com.ugaremin.portfoliowatcher.data

import android.content.Context
import com.ugaremin.portfoliowatcher.data.Room.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class StocksDataSource {

    suspend fun getStocksData(): MutableList<StocksData> {
        val url = "https://borsa.doviz.com/hisseler"
        val doc = withContext(Dispatchers.IO) {
            Jsoup.connect(url).get()
        }

        val table = doc.select("table")
        val tableRows = table.select("tr")

        val data = mutableListOf<StocksData>()

        for (row in tableRows) {
            val columns = row.select("td")
            if (columns.size >= 5) {
                val stockName = columns[0].text().trim()
                val lastValue = columns[2].text().trim()
                val percentChange = columns[6].text().trim()
                val rowData = StocksData(stockName, lastValue, percentChange)
                data.add(rowData)
            }
        }
        return data
    }

    suspend fun getPortfolioData(context: Context): List<StocksData> {
        val url = "https://borsa.doviz.com/hisseler"
        val doc = withContext(Dispatchers.IO) {
            Jsoup.connect(url).get()
        }

        val table = doc.select("table")
        val tableRows = table.select("tr")

        val data = mutableListOf<StocksData>()

        for (row in tableRows) {
            val columns = row.select("td")
            if (columns.size >= 5) {
                val stockName = columns[0].text().trim()
                val lastValue = columns[2].text().trim()
                val percentChange = columns[6].text().trim()
                val name = stockName.take(5)
                val user = AppDatabase.getInstance(context).stocksDao().findStock(name)
                if(user != null){
                    val rowData = StocksData(stockName, lastValue, percentChange)
                    data.add(rowData)
                }

            }
        }
        return data
    }
}