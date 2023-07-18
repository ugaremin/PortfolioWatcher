package com.example.portfoliowatcher.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.portfoliowatcher.AppDatabase
import com.example.portfoliowatcher.R
import com.example.portfoliowatcher.Stocks
import com.example.portfoliowatcher.data.StocksData

class PortfolioAdapter(val context: Context, var stocks: List<StocksData>) : RecyclerView.Adapter<PortfolioAdapter.PortfolioViewHolder>() {


    inner class PortfolioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nameTextView: TextView = itemView.findViewById(R.id.stockNameTextViewPortfolio)
        val detailTextView: TextView = itemView.findViewById(R.id.stockNameDetailTextViewPortfolio)
        val lastValueTextView: TextView = itemView.findViewById(R.id.stockLastValueTextViewPortfolio)
        val changeTextView: TextView = itemView.findViewById(R.id.stockChangeTextViewPortfolio)
        val arrowIcon: ImageView = itemView.findViewById(R.id.arrow_icon_portfolio)
        val deleteStock: ImageView = itemView.findViewById(R.id.delete_stock)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PortfolioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_portfolio, parent, false)
        return PortfolioViewHolder(view)
    }


    override fun getItemCount(): Int {
        return stocks.size
    }

    override fun onBindViewHolder(holder: PortfolioViewHolder, position: Int) {
        val stocks = stocks[position]
        holder.nameTextView.text = stocks.stockName.take(5)
        holder.detailTextView.text = stocks.stockName.drop(5)
        holder.lastValueTextView.text = stocks.lastValue
        holder.changeTextView.text = stocks.percentChange


        val percentChange = stocks.percentChange
        //Todo: Delete butonuna basıldığında alının aksiyon
        holder.deleteStock.setOnClickListener{


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

    fun setStocksSearch(stocks: List<StocksData>) {
        this.stocks = stocks
        notifyDataSetChanged()
    }
}