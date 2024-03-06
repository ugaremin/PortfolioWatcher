package com.ugaremin.portfoliowatcher.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ugaremin.portfoliowatcher.data.Room.AppDatabase
import com.ugaremin.portfoliowatcher.R
import com.ugaremin.portfoliowatcher.data.StocksData
import com.ugaremin.portfoliowatcher.data.TotalPortfolioStatus
import com.ugaremin.portfoliowatcher.ui.portfolio.PortfolioViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PortfolioAdapter(val context: Context, var stocks: MutableList<StocksData>, val viewModel: PortfolioViewModel, private val listener: StockItemClickListener, var onItemClick: () -> Unit) : RecyclerView.Adapter<PortfolioAdapter.PortfolioViewHolder>() {

    var sumResultLastValue: Double = 0.0
    inner class PortfolioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nameTextView: TextView = itemView.findViewById(R.id.stockNameTextViewPortfolio)
        val detailTextView: TextView = itemView.findViewById(R.id.stockNameDetailTextViewPortfolio)
        val lastValueTextView: TextView = itemView.findViewById(R.id.stockLastValueTextViewPortfolio)
        val changeTextView: TextView = itemView.findViewById(R.id.stockChangeTextViewPortfolio)
        val arrowIcon: ImageView = itemView.findViewById(R.id.arrow_icon_portfolio)
        val stockAmountTextView: TextView = itemView.findViewById(R.id.stockAmountTextViewPortfolio)
        val stockCostTextView: TextView = itemView.findViewById(R.id.stockCostTextViewPortfolio)
        val stockCurrentValueTextView: TextView = itemView.findViewById(R.id.stockCurrentValueTextViewPortfolio)
        val stockProfitTextView: TextView = itemView.findViewById(R.id.stockCProfitTextViewPortfolio)

        var stockAmount : Int? = null
        var stockCost : Double? = null
        var stockLastValue: Double? = null
        var resultLastValue: Double? = null

        var resultProfit: Double? = null
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

        if (position == 0){
            sumResultLastValue = 0.0
        }

        val myStocks = stocks[position]
        holder.itemView.setOnClickListener {
            listener.onItemClick(myStocks)
        }
        holder.nameTextView.text = myStocks.stockName.take(5)
        holder.detailTextView.text = myStocks.stockName.drop(5).trim()
        holder.lastValueTextView.text = myStocks.lastValue.replace(".", "").replace(',', '.')
        holder.stockLastValue = myStocks.lastValue.replace(".", "").replace(',', '.').toDouble()
        holder.changeTextView.text = myStocks.percentChange



        GlobalScope.launch {
            launch(Dispatchers.IO){
                val userDao = AppDatabase.getInstance(context).stocksDao()
                val stock = userDao.getStockByName(holder.nameTextView.text.toString())
                holder.stockAmount = stock?.stock_amount
                holder.stockCost =  stock?.stock_total

            }
        }

        GlobalScope.launch {
            launch(Dispatchers.Main) {

                if (holder.stockAmount != null && holder.stockCost != null){
                    holder.resultLastValue = holder.stockLastValue!! * holder.stockAmount!!
                    holder.resultProfit = holder.resultLastValue!! - holder.stockCost!!
                    holder.stockProfitTextView.text = String.format("%.2f", holder.resultProfit)

                    if(holder.stockProfitTextView.text.toString()[0] == '-'){
                        holder.stockProfitTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
                    }else{
                        holder.stockProfitTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.green))
                    }

                    holder.stockCurrentValueTextView.text = String.format("%.2f", holder.resultLastValue)
                    holder.stockAmountTextView.text = holder.stockAmount.toString()
                    holder.stockCostTextView.text = String.format("%.2f", holder.stockCost)
                    sumResultLastValue += (holder.resultLastValue!!)

                    if (position == stocks.size - 1){
                        calculateTotalStatus(sumResultLastValue)
                    }

                }
                
            }
        }


        val percentChange = myStocks.percentChange
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
        val stockName = stocks[position].stockName.take(5)
        viewModel.deleteStocks(context, stockName){
            viewModel.getSumStocks(context)
        }
        stocks.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    fun calculateTotalStatus(sumLastValue: Double){
        TotalPortfolioStatus.sumLastValues = sumLastValue;
        TotalPortfolioStatus.totalProfit = TotalPortfolioStatus.sumLastValues - TotalPortfolioStatus.sumStocks
        TotalPortfolioStatus.totalProfitPercent = (TotalPortfolioStatus.totalProfit / TotalPortfolioStatus.sumStocks) * 100
        Log.i("EMN", "Total Cost: ${TotalPortfolioStatus.sumStocks}")
        onItemClick()

    }

}
