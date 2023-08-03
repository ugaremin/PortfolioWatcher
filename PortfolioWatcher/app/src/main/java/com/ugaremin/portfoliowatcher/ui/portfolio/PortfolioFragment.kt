package com.ugaremin.portfoliowatcher.ui.portfolio

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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ugaremin.portfoliowatcher.AppDatabase
import com.ugaremin.portfoliowatcher.Listener
import com.ugaremin.portfoliowatcher.adapter.PortfolioAdapter
import com.ugaremin.portfoliowatcher.data.StocksData
import com.ugaremin.portfoliowatcher.databinding.FragmentPortfolioBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class PortfolioFragment : Fragment(), Listener {

    private lateinit var viewModel: PortfolioViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PortfolioAdapter

    private var _binding: FragmentPortfolioBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPortfolioBinding.inflate(inflater, container, false)
        val view = binding.root
        val mutableList = mutableListOf<StocksData>()
        adapter = PortfolioAdapter(requireContext(), mutableList)
        binding.portfolioRecyclerView.adapter = adapter
        binding.portfolioRecyclerView.layoutManager = LinearLayoutManager(activity)

        searchBarTextWatcher(binding.searchEditText)

        binding.deleteButton.setOnClickListener{
            deleteCheckedItems()
        }

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

        binding.portfolioRecyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                hideKeyboard(binding.searchEditText)
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })


        binding.portfolioRecyclerView.addItemDecoration(
            DividerItemDecoration(
                activity?.baseContext,
                (binding.portfolioRecyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )



        viewModel = ViewModelProvider(this).get(PortfolioViewModel::class.java)

        viewModel.stocksLiveData.observe(viewLifecycleOwner, Observer { stocks ->

            adapter.stocks = stocks
            adapter.setStocksSearch(stocks)
        })

        viewModel.getAllStocks(requireContext())

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
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
                performSearch(query)
            }
        })

    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun deleteCheckedItems() {
        val checkedItems = adapter.getCheckedItems()
        if (checkedItems.size != 0){
            for (item in checkedItems) {
                adapter.deleteSelectedItems()
                val stockName = item.stockName.take(5)
                GlobalScope.launch {
                    launch (Dispatchers.IO){
                        AppDatabase.getInstance(requireContext()).stocksDao().deleteStockByStockName(stockName)
                    }
                }
            }
        }

    }

    private val listener = object : Listener {
        override fun onItemClicked(position: Int) {
            adapter.toggleItemSelection(position)
        }
    }

    override fun onItemClicked(position: Int) {
        TODO("Not yet implemented")
    }


}