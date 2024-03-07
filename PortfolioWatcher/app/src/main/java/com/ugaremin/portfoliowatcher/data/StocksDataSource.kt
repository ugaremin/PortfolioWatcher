package com.ugaremin.portfoliowatcher.data

import android.content.Context
import com.ugaremin.portfoliowatcher.data.Room.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class StocksDataSource {
    private val urlPattern = """<a\s+href="([^"]+)".*?>""".toRegex()

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
                val lastValue = columns[1].text().trim()
                val percentChange = columns[5].text().trim()
                val stockUrl = columns[0].html().toString()
                val matchResult = urlPattern.find(stockUrl)
                val getUrl = matchResult?.groupValues?.get(1).toString()
                val rowData = StocksData(stockName, lastValue, percentChange, getUrl)
                data.add(rowData)
            }
        }
        return data
    }

    suspend fun getPortfolioData(context: Context): MutableList<StocksData> {
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
                val lastValue = columns[1].text().trim()
                val percentChange = columns[5].text().trim()
                val name = stockName.take(5)
                val stockUrl = columns[0].html().toString()
                val matchResult = urlPattern.find(stockUrl)
                val getUrl = matchResult?.groupValues?.get(1).toString()
                val user = AppDatabase.getInstance(context).stocksDao().findStock(name)
                if(user != null){
                    val rowData = StocksData(stockName, lastValue, percentChange, getUrl)
                    data.add(rowData)
                }

            }
        }
        return data
    }

    suspend fun getStocksDetail(stockUrl: String, stockName: String){
        val url = stockUrl
        val doc = withContext(Dispatchers.IO) {
            Jsoup.connect(url).get()
        }

        val table = doc.select("table")
        val tableRows = table.select("tr")

        for (row in tableRows) {
            val columns = row.select("td")
            if (columns.size >= 4) {
                val weeklyChange = columns[1].text().trim()
                val monthlyChange = columns[2].text().trim()
                val yearlyChange = columns[3].text().trim()
                StockDetailData.stockName = stockName.trim().take(5)
                StockDetailData.weeklyChange = weeklyChange
                StockDetailData.monthlyChange = monthlyChange
                StockDetailData.yearlyChange = yearlyChange

            }
        }
    }
}
