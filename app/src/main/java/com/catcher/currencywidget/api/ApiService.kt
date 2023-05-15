package com.catcher.currencywidget.api

import com.catcher.currencywidget.database.CurrenciesTable
import com.catcher.currencywidget.database.ExchangeRatesTable
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("api/currencies/")
    fun getCurrencies(): Call<List<CurrenciesTable>>

    @GET("api/CurrencyExchangeRates/")
    fun getExchangeRates(): Call<List<ExchangeRatesTable>>
}