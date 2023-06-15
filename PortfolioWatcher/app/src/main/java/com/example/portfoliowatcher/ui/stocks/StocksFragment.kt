package com.example.portfoliowatcher.ui.stocks

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.portfoliowatcher.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup


class StocksFragment : Fragment() {

    val url = "https://borsa.doviz.com/hisseler"
    val TAG = StocksFragment::class.java.simpleName

    data class Stocks(val stockName: String, val lastValue: String, val percentChange: String)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        scrapeWebsite(url)
        return inflater.inflate(R.layout.fragment_stocks, container, false)
    }

    fun scrapeWebsite(url: String) {
        GlobalScope.launch {
            val doc = withContext(Dispatchers.IO) {
                Jsoup.connect(url).get()
            }

            val table = doc.select("table")
            val tableRows = table.select("tr")

            val data = mutableListOf<Stocks>()

            for (row in tableRows) {
                val columns = row.select("td")
                if (columns.size >= 5) {
                    val stockName = columns[0].text()
                    val lastValue = columns[1].text()
                    val percentChange = columns[5].text()
                    val rowData = Stocks(stockName, lastValue, percentChange)
                    data.add(rowData)
                }
            }

            // Tablodan alınan verileri işleyebilir veya kaydedebilirsiniz.
            for (row in data) {
                Log.i(
                    TAG,
                    "${row.stockName}:  Son: ${row.lastValue}, Değişim: ${row.percentChange}"
                )
            }
        }
    }

}