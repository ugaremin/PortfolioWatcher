package com.example.portfoliowatcher.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class StocksDataSource {

    suspend fun getStocksData(): List<StocksData> {
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
                val stockName = columns[0].text()
                val lastValue = columns[1].text()
                val percentChange = columns[5].text()
                val rowData = StocksData(stockName, lastValue, percentChange)
                data.add(rowData)
            }
        }
        return data
    }
}