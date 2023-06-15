package com.example.portfoliowatcher.ui.stocks

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.portfoliowatcher.R
import com.example.portfoliowatcher.data.StocksData


class StocksFragment : Fragment() {

    private lateinit var viewModel: StocksViewModel

    val url = "https://borsa.doviz.com/hisseler"
    val TAG = StocksFragment::class.java.simpleName


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this).get(StocksViewModel::class.java)

        viewModel.stocksLiveData.observe(viewLifecycleOwner, Observer { stocks ->

            for (stock in stocks) {
                Log.i(TAG, "${stock.stockName}:  Son: ${stock.lastValue}, Değişim: ${stock.percentChange}"
                )
            }
        })

        viewModel.uploadStocksData()

        return inflater.inflate(R.layout.fragment_stocks, container, false)
    }

}