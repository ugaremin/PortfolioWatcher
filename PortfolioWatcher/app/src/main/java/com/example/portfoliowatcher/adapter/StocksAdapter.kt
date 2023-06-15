package com.example.portfoliowatcher.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.portfoliowatcher.R
import com.example.portfoliowatcher.data.StocksData

class StocksAdapter(var stocks: List<StocksData>) : RecyclerView.Adapter<StocksAdapter.ViewHolder>()  {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nameTextView: TextView = itemView.findViewById(R.id.stockNameTextView)
        val detailTextView: TextView = itemView.findViewById(R.id.stockNameDetailTextView)
        val lastValueTextView: TextView = itemView.findViewById(R.id.stockLastValueTextView)
        val changeTextView: TextView = itemView.findViewById(R.id.stockChangeTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stock, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val stocks = stocks[position]
        holder.nameTextView.text = stocks.stockName.take(5)
        holder.detailTextView.text = stocks.stockName.drop(5).take(15)
        holder.lastValueTextView.text = stocks.lastValue
        holder.changeTextView.text = stocks.percentChange
    }

    override fun getItemCount(): Int {
        return stocks.size
    }
}