package com.ugaremin.portfoliowatcher.adapter

import com.ugaremin.portfoliowatcher.data.StocksData

interface StockItemClickListener {
    fun onItemClick(item: StocksData)
}