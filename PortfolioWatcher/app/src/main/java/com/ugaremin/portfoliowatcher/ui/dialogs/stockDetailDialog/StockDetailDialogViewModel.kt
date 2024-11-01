package com.ugaremin.portfoliowatcher.ui.dialogs.stockDetailDialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ugaremin.portfoliowatcher.data.BottomSheetItem
import com.ugaremin.portfoliowatcher.data.StockDetailData

class StockDetailDialogViewModel : ViewModel() {

    private val _bottomSheetItems = MutableLiveData<List<BottomSheetItem>>()
    val bottomSheetItems: LiveData<List<BottomSheetItem>> = _bottomSheetItems

    init {
        val items = listOf(
            BottomSheetItem(StockDetailData.stockName),
            BottomSheetItem(StockDetailData.weeklyChange),
            BottomSheetItem(StockDetailData.monthlyChange),
            BottomSheetItem(StockDetailData.yearlyChange)
        )
        _bottomSheetItems.value = items
    }
}