package com.ugaremin.portfoliowatcher.ui.portfolio

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.ugaremin.portfoliowatcher.Utilities.NetworkCheck.Companion.isInternetAvailable
import com.ugaremin.portfoliowatcher.R
import com.ugaremin.portfoliowatcher.Utilities.CheckBottomSheetDialog
import com.ugaremin.portfoliowatcher.Utilities.CustomItemDecoration
import com.ugaremin.portfoliowatcher.Utilities.NetworkMonitorService
import com.ugaremin.portfoliowatcher.adapter.PortfolioAdapter
import com.ugaremin.portfoliowatcher.adapter.StockItemClickListener
import com.ugaremin.portfoliowatcher.adapter.SwipeToDeleteCallback
import com.ugaremin.portfoliowatcher.data.StockDetailData
import com.ugaremin.portfoliowatcher.data.StocksData
import com.ugaremin.portfoliowatcher.data.TotalPortfolioStatus
import com.ugaremin.portfoliowatcher.databinding.FragmentPortfolioBinding
import com.ugaremin.portfoliowatcher.ui.dialogs.stockDetailDialog.StockDetailDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class PortfolioFragment : Fragment(), StockItemClickListener {

    private val TAG = PortfolioFragment::class.java.simpleName;
    private lateinit var viewModel: PortfolioViewModel
    private lateinit var adapter: PortfolioAdapter
    private lateinit var networkMonitorService: NetworkMonitorService

    private var _binding: FragmentPortfolioBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPortfolioBinding.inflate(inflater, container, false)
        val view = binding.root
        val mutableList = mutableListOf<StocksData>()
        viewModel = ViewModelProvider(this).get(PortfolioViewModel::class.java)
        adapter = PortfolioAdapter(requireContext(), mutableList, viewModel,this)
        binding.portfolioProgressBar.visibility = View.VISIBLE
        binding.portfolioRecyclerView.adapter = adapter
        binding.portfolioRecyclerView.layoutManager = LinearLayoutManager(activity)
        val itemDecoration = CustomItemDecoration(resources.getDimensionPixelSize(R.dimen.item_offset))
        binding.portfolioRecyclerView.addItemDecoration(itemDecoration)

        viewModel.stocksLiveData.observe(viewLifecycleOwner, Observer { stocks ->

            adapter.stocks = stocks
            adapter.setStocksSearch(stocks)
            if (stocks.size > 0){
                binding.portfolioFragmentWarningText.visibility = View.GONE
                binding.totalStatusLayout.visibility = View.VISIBLE
            }
            else {
                binding.portfolioFragmentWarningText.visibility = View.VISIBLE
                binding.totalStatusLayout.visibility = View.GONE
            }
        })
        viewModel.getSumLastValue(requireContext()){
            GlobalScope.launch(Dispatchers.Main) {
                setGeneralSituationValeu()
                binding.portfolioProgressBar.visibility = View.GONE
            }

        }


        viewModel.setContext(requireContext())

        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapter))
        itemTouchHelper.attachToRecyclerView(binding.portfolioRecyclerView)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.stopDatabaseRequest()
        networkMonitorService.stopNetworkCallback()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.updateGeneralValuesLiveData.observe(viewLifecycleOwner, Observer {
            setGeneralSituationValeu()
        })

        context?.let { context ->
            networkMonitorService = NetworkMonitorService(context) { isConnected ->
                if (isConnected) {
                    viewModel.startDatabaseRequest()
                    setViewAccordingToNetworkStatus(true)
                } else {
                    viewModel.stopDatabaseRequest()
                    setViewAccordingToNetworkStatus(false)
                }
            }
        }
    }

    override fun onItemClick(item: StocksData) {
        viewModel.uploadStockDetail(item.stockUrl, item.stockName, ){ success ->
            if (success && CheckBottomSheetDialog.dialogIsShowing != true){
                val bottomSheetDialogFragment = StockDetailDialogFragment()
                bottomSheetDialogFragment.show(childFragmentManager, bottomSheetDialogFragment.tag)
                CheckBottomSheetDialog.dialogIsShowing = true
                Log.d(TAG, "Stock Details -> weekly: ${StockDetailData.weeklyChange} -- monthly: ${StockDetailData.monthlyChange} -- yearly: ${StockDetailData.yearlyChange}")
            } else {
                Log.e(TAG, "Stock details could not be fetched")
            }

        }
    }

    private fun setViewAccordingToNetworkStatus(isConnected: Boolean) {
        requireActivity().runOnUiThread {
            if (isConnected) {
                binding.portfolioFragmentDisconnectedView.visibility = View.GONE
                binding.portfolioFragmentConnectedView.visibility = View.VISIBLE
            } else {
                binding.portfolioFragmentDisconnectedView.visibility = View.VISIBLE
                binding.portfolioFragmentConnectedView.visibility = View.GONE
            }
        }
    }

    private fun setGeneralSituationValeu() {

        binding.totalCostTextView.text = String.format("%.2f", TotalPortfolioStatus.sumStocks)
        binding.totalValueTextView.text = String.format("%.2f", TotalPortfolioStatus.sumLastValues)
        binding.totalProfitTextView.text = String.format("%.2f", TotalPortfolioStatus.totalProfit)
        setTextViewColorBySecondCharacter(TotalPortfolioStatus.totalProfit, binding.totalProfitTextView)
        binding.totalPercentTextView.text = ("%" + String.format("%.2f", TotalPortfolioStatus.totalProfitPercent))
        setTextViewColorBySecondCharacter(TotalPortfolioStatus.totalProfitPercent, binding.totalPercentTextView)

    }

    fun setTextViewColorBySecondCharacter(value: Double, textView: TextView?) {
        val text = String.format("%.2f", value)

        if (text == "0,00") {
            textView?.setTextColor(Color.GRAY)
        } else if (text.length >= 2 && text[0] == '-') {
            textView?.setTextColor(textView.context.getColor(R.color.red))
        } else {
            textView?.setTextColor(textView.context.getColor(R.color.green))
        }
    }

}