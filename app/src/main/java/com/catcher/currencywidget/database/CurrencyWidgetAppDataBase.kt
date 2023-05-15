package com.catcher.currencywidget.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CurrenciesTable::class,ExchangeRatesTable::class], version = 1, exportSchema = false)
abstract class CurrencyWidgetAppDataBase: RoomDatabase() {
    abstract fun currencies(): CurrenciesTableDAO
    abstract fun currenciesExchange(): ExchangeRatesTableDAO

    companion object {
        @Volatile
        private var INSTANCE: CurrencyWidgetAppDataBase? = null

        fun getDatabase(context: Context): CurrencyWidgetAppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CurrencyWidgetAppDataBase::class.java,
                    "currency_widget_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}