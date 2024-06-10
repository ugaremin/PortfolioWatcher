package com.ugaremin.portfoliowatcher.data

import android.content.Context
import android.util.Log
import com.ugaremin.portfoliowatcher.data.Room.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class StocksDataSource {
    private val urlPattern = """<a\s+href="([^"]+)".*?>""".toRegex()

    suspend fun getStocksData(context: Context, forPortfolio: Boolean): MutableList<StocksData> {
        val url = "https://borsa.doviz.com/hisseler"
        var rowData: StocksData
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
                if (forPortfolio) {
                    val user = AppDatabase.getInstance(context).stocksDao().findStock(name)
                    if(user != null){
                        rowData = StocksData(stockName, lastValue, percentChange, getUrl)
                        data.add(rowData)
                    }
                } else {
                    rowData = StocksData(stockName, lastValue, percentChange, getUrl)
                    data.add(rowData)
                }

            }
        }
        return data
    }
    suspend fun getTotalValueForPortfolio(context: Context) : Double{

        val url = "https://borsa.doviz.com/hisseler"
        var tempResult = 0.0
        val doc = withContext(Dispatchers.IO) {
            Jsoup.connect(url).get()
        }

        val table = doc.select("table")
        val tableRows = table.select("tr")

        for (row in tableRows) {
            val columns = row.select("td")
            if (columns.size >= 5) {
                val stockName = columns[0].text().trim()
                val lastValue = columns[1].text().trim().replace(".", "").replace(',', '.').toDouble()
                val name = stockName.take(5)
                val user = AppDatabase.getInstance(context).stocksDao().findStock(name)
                if(user != null){
                    val amountValue = AppDatabase.getInstance(context).stocksDao().getValueByName(name)
                    tempResult += amountValue * lastValue
                }


            }
        }
        TotalPortfolioStatus.sumLastValues = tempResult
        return tempResult
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
            StockDetailData.stockName = stockName.trim().take(5)
            var weeklyChange: String? = null
            var monthlyChange: String? = null
            var yearlyChange: String? = null
            if (columns.size == 2) {
                if(columns.text().toString().contains("Haftalık")){
                    weeklyChange = columns[1].text().trim()
                }

                if(columns.text().toString().contains("Aylık")){
                    monthlyChange = columns[1].text().trim()
                }

                if(columns.text().toString().contains("Yıllık")){
                    yearlyChange = columns[1].text().trim()
                }

                if (weeklyChange != null) {
                    StockDetailData.weeklyChange = weeklyChange
                }
                if (monthlyChange != null) {
                    StockDetailData.monthlyChange = monthlyChange
                }
                if (yearlyChange != null) {
                    StockDetailData.yearlyChange = yearlyChange
                }

            }
        }
    }
}
