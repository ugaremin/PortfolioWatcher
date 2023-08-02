package com.ugaremin.portfoliowatcher.adapter

import android.content.Context
import android.os.Bundle
import android.provider.Settings.Global
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ugaremin.portfoliowatcher.AppDatabase
import com.ugaremin.portfoliowatcher.R
import com.ugaremin.portfoliowatcher.Stocks
import com.ugaremin.portfoliowatcher.StocksDao
import com.ugaremin.portfoliowatcher.data.StocksData
import com.ugaremin.portfoliowatcher.ui.dialogs.AddStockFragment
import com.ugaremin.portfoliowatcher.ui.portfolio.PortfolioFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class StocksAdapter(val context: Context, var stocks: List<StocksData>) : RecyclerView.Adapter<StocksAdapter.ViewHolder>()  {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nameTextView: TextView = itemView.findViewById(R.id.stockNameTextView)
        val detailTextView: TextView = itemView.findViewById(R.id.stockNameDetailTextView)
        val lastValueTextView: TextView = itemView.findViewById(R.id.stockLastValueTextView)
        val changeTextView: TextView = itemView.findViewById(R.id.stockChangeTextView)
        val arrowIcon: ImageView = itemView.findViewById(R.id.arrow_icon)
        val addStock: ImageView = itemView.findViewById(R.id.add_stock)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stock, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val stocks = stocks[position]
        holder.nameTextView.text = stocks.stockName.take(5)
        holder.detailTextView.text = stocks.stockName.drop(5)
        holder.lastValueTextView.text = stocks.lastValue
        holder.changeTextView.text = stocks.percentChange


        val percentChange = stocks.percentChange
        //Todo: Ekleme butonuna basıldığında alının aksiyon
        holder.addStock.setOnClickListener{

            val itemName = stocks.stockName.take(5)
            val stock = Stocks(0,itemName)
            persistStock(stock)
            holder.addStock.setImageResource(R.drawable.add_stock_success)
            holder.addStock.isClickable = false


        }

        if (percentChange[1] == '-') {
            holder.changeTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
            holder.arrowIcon.setBackgroundResource(R.drawable.arrow_down)
        }else if(percentChange == "%0,00"){
            holder.arrowIcon.setBackgroundResource(R.drawable.arrow_line)
            holder.changeTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.grey))
        }else {
            holder.changeTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.green))
            holder.arrowIcon.setBackgroundResource(R.drawable.arrow_up)
        }
    }

    override fun getItemCount(): Int {
        return stocks.size
    }

    fun setStocksSearch(stocks: List<StocksData>) {
        this.stocks = stocks
        notifyDataSetChanged()
    }

    private fun persistStock(stocks: Stocks){
        GlobalScope.launch {
            launch(Dispatchers.IO){
                val userDao = AppDatabase.getInstance(context).stocksDao()
                userDao.insert(stocks)


            }

        }
    }
}