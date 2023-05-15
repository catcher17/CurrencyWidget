package com.catcher.currencywidget.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface CurrenciesTableDAO {
    @Query("SELECT * FROM currencies")
    fun getAll(): List<CurrenciesTable>

    @Query("SELECT * FROM currencies")
    fun getAllSingle(): Single<List<CurrenciesTable>>

    @Query("SELECT * FROM currencies WHERE id = :currencyId")
    fun findByCurrencyId(): Single<List<CurrenciesTable>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(data: List<CurrenciesTable>)
}