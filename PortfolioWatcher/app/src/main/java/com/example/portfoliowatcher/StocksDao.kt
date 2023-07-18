package com.example.portfoliowatcher

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StocksDao {

    @Insert
    fun insert(stocks: Stocks)

    @Delete
    fun delete(stocks: Stocks)

    @Query("SELECT * FROM stocks")
    fun getAll(): List<Stocks>

}