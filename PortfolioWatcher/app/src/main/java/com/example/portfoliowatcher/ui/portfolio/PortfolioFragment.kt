package com.example.portfoliowatcher.ui.portfolio

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.portfoliowatcher.AppDatabase
import com.example.portfoliowatcher.R
import com.example.portfoliowatcher.StocksDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PortfolioFragment : Fragment() {
    private lateinit var stocksDao: StocksDao


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val database = AppDatabase.getInstance(requireContext())
        stocksDao = database.stocksDao()

        lifecycleScope.launch(Dispatchers.IO) {
            val strings = stocksDao.getAll()
            withContext(Dispatchers.Main) {

                for (stringEntity in strings) {
                    // Sadece değerleri log olarak bastırın
                    Log.d("EMN", stringEntity.stock_name)
                }
            }

        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_portfolio, container, false)
    }

}