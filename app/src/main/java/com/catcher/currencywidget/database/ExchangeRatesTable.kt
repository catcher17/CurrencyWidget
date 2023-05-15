package com.catcher.currencywidget.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey


@Entity(tableName = "exchange_rates",
    foreignKeys = [
        ForeignKey(
            entity = CurrenciesTable::class,
            parentColumns = ["id"],
            childColumns = ["baseCurrency"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CurrenciesTable::class,
            parentColumns = ["id"],
            childColumns = ["targetCurrency"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ])
data class ExchangeRatesTable(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val baseCurrency:Int,
    val targetCurrency:Int,
    val rate:Double,
    val date:String)