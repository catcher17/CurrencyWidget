package com.catcher.currencywidget.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Single

@Dao
interface ExchangeRatesTableDAO {
    @Query("SELECT * FROM exchange_rates")
    fun getAll(): List<ExchangeRatesTable>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(data: List<ExchangeRatesTable>)

    @Query("SELECT * FROM exchange_rates WHERE baseCurrency = :fromCurrency AND targetCurrency = :toCurrency")
    fun getExchangeRateSingle(fromCurrency: Int, toCurrency: Int): Single<ExchangeRatesTable>
}