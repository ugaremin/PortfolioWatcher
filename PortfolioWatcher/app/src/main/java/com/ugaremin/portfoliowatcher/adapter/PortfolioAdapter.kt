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
        val stockAmountTextView: TextView = itemView.findViewById(R.id.stockAmountTextViewPortfolio)
        val stockCostTextView: TextView = itemView.findViewById(R.id.stockCostTextViewPortfolio)
        val stockCurrentValueTextView: TextView = itemView.findViewById(R.id.stockCurrentValueTextViewPortfolio)
        val stockProfitTextView: TextView = itemView.findViewById(R.id.stockCProfitTextViewPortfolio)

        var stockAmount : Int? = null
        var stockCost : Double? = null
        var stockCurrentValue : Double? = null
        var stockProfit: Double? = null
        var stockLastValue: Double? = null
        var resultLastValue: Double? = null
        var resultProfit: Double? = null

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
        holder.detailTextView.text = stocks.stockName.drop(5).trim()
        holder.lastValueTextView.text = stocks.lastValue.replace(',', '.')
        holder.stockLastValue = stocks.lastValue.replace(',', '.').toDouble()
        holder.changeTextView.text = stocks.percentChange
        holder.itemCheck.isChecked = checkedItems.contains(position)




        GlobalScope.launch {
            launch(Dispatchers.IO){
                val userDao = AppDatabase.getInstance(context).stocksDao()
                val stock = userDao.getStockByName(holder.nameTextView.text.toString())
                holder.stockAmount = stock?.stock_amount
                holder.stockCost =  stock?.stock_total
                holder.resultLastValue = holder.stockLastValue!! * holder.stockAmount!!
                holder.resultProfit = holder.resultLastValue!! - holder.stockCost!!


            }
        }

        GlobalScope.launch {
            launch(Dispatchers.Main) {
                holder.stockProfitTextView.text = String.format("%.2f", holder.resultProfit)

                if(holder.stockProfitTextView.text.toString()[0] == '-'){
                    holder.stockProfitTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
                }else{
                    holder.stockProfitTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.green))
                }

                holder.stockCurrentValueTextView.text = String.format("%.2f", holder.resultLastValue)
                holder.stockAmountTextView.text = holder.stockAmount.toString()
                holder.stockCostTextView.text = String.format("%.2f", holder.stockCost)

            }
        }



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


    fun getCheckedItems(): List<StocksData> {
        val result = mutableListOf<StocksData>()
        for (index in checkedItems) {
            if (index >= 0 && index < stocks.size) {
                result.add(stocks[index])
            }
        }
        return result
    }

    fun deleteSelectedItems() {
        val sortedSelectedItems = checkedItems.toList().sortedDescending()
        for (position in sortedSelectedItems) {
            stocks.removeAt(position)
            notifyItemRemoved(position)
        }
        if(checkedItems.size != 0){
            checkedItems.clear()
        }
        
    }
}
