package com.catcher.currencywidget


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.catcher.currencywidget.api.ApiClient
import com.catcher.currencywidget.database.CurrenciesTableDAO
import com.catcher.currencywidget.database.CurrencyWidgetAppDataBase
import com.catcher.currencywidget.database.ExchangeRatesTableDAO
import com.google.android.material.navigation.NavigationBarView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: NavigationBarView
    private lateinit var db: CurrencyWidgetAppDataBase
    private lateinit var currencyDao: CurrenciesTableDAO
    private lateinit var currencyExchangeDAO: ExchangeRatesTableDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener(onNavigationItemSelectedListener)
        openFragment(MainConverterFragment())
        db = CurrencyWidgetAppDataBase.getDatabase(applicationContext)
        currencyDao = db.currencies()
        currencyExchangeDAO = db.currenciesExchange()
        // Загрузка валют с сервера и сохранение в базу данных
        CoroutineScope(Dispatchers.IO).launch {
            fetchCurrenciesAndSaveToDb()
            fetchExchangeRatesAndSaveToDb()

        }
    }

    private val onNavigationItemSelectedListener = NavigationBarView.OnItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_bar_converter -> {
                openFragment(MainConverterFragment())
                return@OnItemSelectedListener true
            }
            R.id.navigation_bar_currencies -> {
                openFragment(CurrenciesFragment())
                return@OnItemSelectedListener true
            }
            R.id.navigation_bar_settings-> {
                openFragment(CurrenciesFragment())
                return@OnItemSelectedListener true
            }
        }
        false
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun fetchCurrenciesAndSaveToDb() {
        try {
            val response = ApiClient.apiService.getCurrencies().execute()
            if (response.isSuccessful) {
                val currencies = response.body()
                if (currencies != null) {
                    currencyDao.insertAll(currencies)
                    val currenciesTable = db.currencies().getAll()
                    currenciesTable.forEach { currenciesTableElement->
                        Log.d("CurrencyData", "ID: ${currenciesTableElement.id}, Code: ${currenciesTableElement.code}, Name: ${currenciesTableElement.name}, Name_ru: ${currenciesTableElement.name_ru}")
                    }
                }
            }
        }catch (e: Exception)
        {
            Log.e("API_Error", "Exception: ${e.message}")
        }
    }

    private fun fetchExchangeRatesAndSaveToDb() {
        try {
            val response = ApiClient.apiService.getExchangeRates().execute()
            if (response.isSuccessful) {
                val exchangeRates = response.body()
                if (exchangeRates != null) {
                    currencyExchangeDAO.insertAll(exchangeRates)
                    val exchangeRatesTable= db.currenciesExchange().getAll()
                    exchangeRatesTable.forEach { currenciesExchangeTableElement->
                        Log.d("CurrencyData", "ID: ${currenciesExchangeTableElement.id}, baseCurrency: ${currenciesExchangeTableElement.baseCurrency}, target: ${currenciesExchangeTableElement.targetCurrency}, rate: ${currenciesExchangeTableElement.rate}, date: ${currenciesExchangeTableElement.date}")
                    }
                }
            }
        }catch (e: Exception)
        {
            Log.e("API_Error", "Exception: ${e.message}")
        }
    }
}
