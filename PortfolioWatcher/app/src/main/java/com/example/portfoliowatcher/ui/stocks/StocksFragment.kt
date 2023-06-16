package com.example.portfoliowatcher.ui.stocks

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
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
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.portfoliowatcher.R
import com.example.portfoliowatcher.adapter.StocksAdapter


class StocksFragment : Fragment() {

    private lateinit var viewModel: StocksViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StocksAdapter
    private lateinit var searchEditText: EditText
    private lateinit var clearEditText: ImageView
    private lateinit var stocksLayout: ConstraintLayout

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stocks, container, false)

        // Inflate the layout for this fragment
        recyclerView = view.findViewById(R.id.recyclerView)
        adapter = StocksAdapter(emptyList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
        clearEditText = view.findViewById(R.id.cancel_button)
        stocksLayout = view.findViewById(R.id.stocks_layout)
        searchEditText = view.findViewById(R.id.searchEditText)

        clearEditText.setOnClickListener(View.OnClickListener {
            searchEditText.setText("")
            hideKeyboard()
        })


        stocksLayout.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {

                hideKeyboard()
            }
            false
        }

        recyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                hideKeyboard()

                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })


        recyclerView.addItemDecoration(
            DividerItemDecoration(
                activity?.baseContext,
                (recyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )



        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                performSearch(query)
            }
        })

        viewModel = ViewModelProvider(this).get(StocksViewModel::class.java)

        viewModel.stocksLiveData.observe(viewLifecycleOwner, Observer { stocks ->

            adapter.stocks = stocks
            adapter.setStocksSearch(stocks)
        })

        viewModel.uploadStocksData()

        return view
    }

    private fun performSearch(query: String) {
        val filteredList = viewModel.stocksLiveData.value?.filter { hisseSenedi ->
            hisseSenedi.stockName.contains(query, ignoreCase = true)
        }
        filteredList?.let { adapter.setStocksSearch(it) }
    }

    private fun hideKeyboard() {
        searchEditText.clearFocus()
        val imm = requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

}
