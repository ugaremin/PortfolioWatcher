package com.ugaremin.portfoliowatcher.ui.portfolio

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.ugaremin.portfoliowatcher.Utilities.NetworkCheck.Companion.isInternetAvailable
import com.ugaremin.portfoliowatcher.R
import com.ugaremin.portfoliowatcher.Utilities.CustomItemDecoration
import com.ugaremin.portfoliowatcher.adapter.PortfolioAdapter
import com.ugaremin.portfoliowatcher.adapter.StockItemClickListener
import com.ugaremin.portfoliowatcher.adapter.SwipeToDeleteCallback
import com.ugaremin.portfoliowatcher.data.StockDetailData
import com.ugaremin.portfoliowatcher.data.StocksData
import com.ugaremin.portfoliowatcher.databinding.FragmentPortfolioBinding
import com.ugaremin.portfoliowatcher.ui.dialogs.stockDetailDialog.StockDetailDialogFragment


class PortfolioFragment : Fragment(), StockItemClickListener {

    private val TAG = PortfolioFragment::class.java.simpleName;
    private lateinit var viewModel: PortfolioViewModel
    private lateinit var adapter: PortfolioAdapter

    private var _binding: FragmentPortfolioBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPortfolioBinding.inflate(inflater, container, false)
        val view = binding.root
        val mutableList = mutableListOf<StocksData>()
        adapter = PortfolioAdapter(requireContext(), mutableList, this)
        binding.portfolioRecyclerView.adapter = adapter
        binding.portfolioRecyclerView.layoutManager = LinearLayoutManager(activity)
        val itemDecoration = CustomItemDecoration(resources.getDimensionPixelSize(R.dimen.item_offset))
        binding.portfolioRecyclerView.addItemDecoration(itemDecoration)
        viewModel = ViewModelProvider(this).get(PortfolioViewModel::class.java)

        viewModel.stocksLiveData.observe(viewLifecycleOwner, Observer { stocks ->

            adapter.stocks = stocks
            adapter.setStocksSearch(stocks)
            if (stocks.size > 0)
                binding.portfolioFragmentWarningText.visibility = View.GONE
            else
                binding.portfolioFragmentWarningText.visibility = View.VISIBLE
        })

        viewModel.setContext(requireContext())
        if(isInternetAvailable(requireContext())){
            viewModel.startDatabaseRequest()
        }else{
            Toast.makeText(requireContext(), getString(R.string.network_error), Toast.LENGTH_LONG).show()
        }

        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapter))
        itemTouchHelper.attachToRecyclerView(binding.portfolioRecyclerView)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.stopDatabaseRequest()
        _binding = null
    }

    override fun onItemClick(item: StocksData) {
        viewModel.uploadStockDetail(item.stockUrl, item.stockName, ){ success ->
            if (success){
                val bottomSheetDialogFragment = StockDetailDialogFragment()
                bottomSheetDialogFragment.show(childFragmentManager, bottomSheetDialogFragment.tag)
                Log.d(TAG, "Stock Details -> weekly: ${StockDetailData.weeklyChange} -- monthly: ${StockDetailData.monthlyChange} -- yearly: ${StockDetailData.yearlyChange}")
            } else {
                Log.e(TAG, "Stock details could not be fetched")
            }

        }
    }

}