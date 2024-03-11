package com.ugaremin.portfoliowatcher.data.Room

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

    @Query("SELECT * FROM stocks WHERE name = :searchStock")
    fun findStock(searchStock: String): Stocks?

    @Query("DELETE FROM stocks WHERE name = :searchName")
    fun deleteStockByStockName(searchName: String)

    @Query("DELETE FROM stocks")
    fun deleteAllUser()

    @Query("SELECT * FROM stocks WHERE name = :searchName")
    fun getStockByName(searchName: String): Stocks?

    @Query("SELECT SUM(total) FROM stocks")
    fun getTotal(): Double

    @Query("SELECT amount FROM stocks WHERE name = :name")
    fun getValueByName(name: String): Int

}
