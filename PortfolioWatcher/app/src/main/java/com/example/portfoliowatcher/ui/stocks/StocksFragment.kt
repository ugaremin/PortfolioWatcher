package com.example.portfoliowatcher.ui.stocks

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.portfoliowatcher.R
import com.example.portfoliowatcher.adapter.StocksAdapter
import com.example.portfoliowatcher.data.StocksData


class StocksFragment : Fragment() {

    private lateinit var viewModel: StocksViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StocksAdapter

    val url = "https://borsa.doviz.com/hisseler"
    val TAG = StocksFragment::class.java.simpleName


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stocks, container, false)

        // Inflate the layout for this fragment
        recyclerView = view.findViewById(R.id.recyclerView)
        adapter = StocksAdapter(emptyList()) // Başlangıçta boş bir liste ile adapter'ı oluşturun
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        viewModel = ViewModelProvider(this).get(StocksViewModel::class.java)

        viewModel.stocksLiveData.observe(viewLifecycleOwner, Observer { stocks ->

            adapter.stocks = stocks // Yeni hisse senedi verilerini adapter'a atayın
            adapter.notifyDataSetChanged()

            for (stock in stocks) {
                Log.i(TAG, "${stock.stockName}:  Son: ${stock.lastValue}, Değişim: ${stock.percentChange}"
                )
            }
        })

        viewModel.uploadStocksData()

        return view
    }

}