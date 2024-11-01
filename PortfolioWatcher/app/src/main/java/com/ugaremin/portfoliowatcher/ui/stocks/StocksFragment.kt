package com.ugaremin.portfoliowatcher.ui.stocks

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ugaremin.portfoliowatcher.Utilities.NetworkCheck
import com.ugaremin.portfoliowatcher.R
import com.ugaremin.portfoliowatcher.Utilities.CheckBottomSheetDialog
import com.ugaremin.portfoliowatcher.Utilities.CustomItemDecoration
import com.ugaremin.portfoliowatcher.Utilities.NetworkMonitorService
import com.ugaremin.portfoliowatcher.adapter.StockItemClickListener
import com.ugaremin.portfoliowatcher.adapter.StocksAdapter
import com.ugaremin.portfoliowatcher.data.StockDetailData
import com.ugaremin.portfoliowatcher.data.StocksData
import com.ugaremin.portfoliowatcher.databinding.FragmentStocksBinding
import com.ugaremin.portfoliowatcher.ui.dialogs.stockDetailDialog.StockDetailDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class StocksFragment : Fragment(), StockItemClickListener {

    private val TAG = StocksFragment::class.java.simpleName
    private lateinit var viewModel: StocksViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StocksAdapter
    private lateinit var networkMonitorService: NetworkMonitorService

    private var _binding: FragmentStocksBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentStocksBinding.inflate(inflater, container, false)
        val view = binding.root

        adapter = StocksAdapter(requireContext(), emptyList(), this)
        binding.stocksProgressBar.visibility = View.VISIBLE
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        val itemDecoration = CustomItemDecoration(resources.getDimensionPixelSize(R.dimen.item_offset))
        binding.recyclerView.addItemDecoration(itemDecoration)

        searchBarTextWatcher(binding.searchEditText)

        binding.cancelButton.setOnClickListener(View.OnClickListener {
            binding.searchEditText.setText("")
            hideKeyboard(binding.searchEditText)
        })


        binding.stocksLayout.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {

                hideKeyboard(binding.searchEditText)
            }
            false
        }

        binding.recyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                hideKeyboard(binding.searchEditText)

                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })



        viewModel = ViewModelProvider(this).get(StocksViewModel::class.java)

        viewModel.stocksLiveData.observe(viewLifecycleOwner, Observer { stocks ->

            adapter.stocks = stocks
            adapter.setStocksSearch(stocks)
            binding.stocksProgressBar.visibility = View.GONE
        })
        viewModel.setContext(requireContext())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

    override fun onDestroyView() {
        super.onDestroyView()
        networkMonitorService.stopNetworkCallback()
        viewModel.stopDatabaseRequest()
        _binding = null
    }

    private fun performSearch(query: String) {
        val filteredList = viewModel.stocksLiveData.value?.filter { stocks ->
            stocks.stockName.contains(query, ignoreCase = true)
        }
        filteredList?.let { adapter.setStocksSearch(it) }
    }

    private fun hideKeyboard(editText: EditText) {
        editText.clearFocus()
        val imm = requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun searchBarTextWatcher(editText: EditText){

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                if(s.toString() != ""){
                    viewModel.stopDatabaseRequest()
                }else{
                    viewModel.startDatabaseRequest()
                }
                performSearch(query)
            }
        })

    }

    override fun onItemClick(item: StocksData) {
        Log.d(TAG, "Item clicked: ${item.stockUrl}")
        viewModel.uploadStockDetail(item.stockUrl, item.stockName){ success ->
            if (success && CheckBottomSheetDialog.dialogIsShowing != true) {
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
        GlobalScope.launch(Dispatchers.Main) {
            if (isConnected) {
                binding.stocksFragmentDisconnectedView.visibility = View.GONE
                binding.stocksFragmentConnectedView.visibility = View.VISIBLE
            } else {
                binding.stocksFragmentDisconnectedView.visibility = View.VISIBLE
                binding.stocksFragmentConnectedView.visibility = View.GONE
            }
        }
    }

}
