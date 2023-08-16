package com.ugaremin.portfoliowatcher.data.Room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Stocks(
    @PrimaryKey(autoGenerate = true) val uid: Long,
    @ColumnInfo(name = "name") val stock_name: String,
    @ColumnInfo(name = "amount") val stock_amount: Int,
    @ColumnInfo(name = "total") val stock_total: Double,
)
