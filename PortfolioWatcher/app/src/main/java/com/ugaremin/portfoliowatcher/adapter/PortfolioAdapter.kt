package com.ugaremin.portfoliowatcher.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ugaremin.portfoliowatcher.AppDatabase
import com.ugaremin.portfoliowatcher.Listener
import com.ugaremin.portfoliowatcher.R
import com.ugaremin.portfoliowatcher.Stocks
import com.ugaremin.portfoliowatcher.data.StocksData
import com.ugaremin.portfoliowatcher.ui.portfolio.PortfolioFragment
import com.ugaremin.portfoliowatcher.ui.portfolio.PortfolioViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PortfolioAdapter(val context: Context, var stocks: MutableList<StocksData>) : RecyclerView.Adapter<PortfolioAdapter.PortfolioViewHolder>() {

    private val checkedItems = mutableSetOf<Int>()
    inner class PortfolioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nameTextView: TextView = itemView.findViewById(R.id.stockNameTextViewPortfolio)
        val detailTextView: TextView = itemView.findViewById(R.id.stockNameDetailTextViewPortfolio)
        val lastValueTextView: TextView = itemView.findViewById(R.id.stockLastValueTextViewPortfolio)
        val changeTextView: TextView = itemView.findViewById(R.id.stockChangeTextViewPortfolio)
        val arrowIcon: ImageView = itemView.findViewById(R.id.arrow_icon_portfolio)
        val itemCheck: CheckBox = itemView.findViewById(R.id.checkBox)

        init {
            itemCheck.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    (itemView.context as? Listener)?.onItemClicked(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PortfolioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_portfolio, parent, false)
        return PortfolioViewHolder(view)
    }


    override fun getItemCount(): Int {
        return stocks.size
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onBindViewHolder(holder: PortfolioViewHolder, position: Int) {
        val stocks = stocks[position]
        holder.nameTextView.text = stocks.stockName.take(5)
        holder.detailTextView.text = stocks.stockName.drop(5)
        holder.lastValueTextView.text = stocks.lastValue
        holder.changeTextView.text = stocks.percentChange
        holder.itemCheck.isChecked = checkedItems.contains(position)

        holder.itemCheck.setOnClickListener {
            if (holder.itemCheck.isChecked) {
                checkedItems.add(position)
                Log.d("EMN" , "EKLENDİ")
            } else {
                checkedItems.remove(position)
                Log.d("EMN" , "SİLİNDİ !!!")
            }
        }


        val percentChange = stocks.percentChange
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
        this.stocks = stocks.toMutableList()
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        stocks.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getCheckedItems(): List<StocksData> {
        return checkedItems.map { stocks[it] }
    }

    fun deleteSelectedItems() {
        val sortedSelectedItems = checkedItems.toList().sortedDescending()
        for (position in sortedSelectedItems) {
            stocks.removeAt(position)
            notifyItemRemoved(position)
        }
        checkedItems.clear()
    }

    fun toggleItemSelection(position: Int) {
        if (checkedItems.contains(position)) {
            checkedItems.remove(position)
        } else {
            checkedItems.add(position)
        }
        notifyItemChanged(position)
    }
}
