package com.catcher.currencywidget

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.catcher.currencywidget.database.CurrencyWidgetAppDataBase
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class MainConverterFragment : Fragment() {
    private lateinit var recyclerViewFrom: RecyclerView
    private lateinit var recyclerViewTo: RecyclerView
    private lateinit var currencyAdapterWheelFrom: CurrencyAdapterWheel
    private lateinit var currencyAdapterWheelTo: CurrencyAdapterWheel
    private lateinit var layoutManagerFrom: LinearLayoutManager
    private lateinit var layoutManagerTo: LinearLayoutManager
    private var lastPositionFrom: Int? = null
    private var lastPositionTo: Int? = null
    private lateinit var searchEditTextFrom: EditText
    private lateinit var searchEditTextTo: EditText
    private lateinit var exchangeRateTextView: TextView
    private lateinit var editTextFromCurrency: EditText
    private lateinit var exchangeResultTextView: TextView
    private val searchSubjectFrom: PublishSubject<String> = PublishSubject.create()
    private val searchSubjectTo: PublishSubject<String> = PublishSubject.create()
    private val compositeDisposable = CompositeDisposable()
    private lateinit var dataBase:CurrencyWidgetAppDataBase
    private var exchangeRateValue:Double=0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBase = CurrencyWidgetAppDataBase.getDatabase(requireContext())
        val rootView = inflater.inflate(R.layout.fragment_main_coverter, container, false)
        recyclerViewFrom = rootView.findViewById(R.id.currenciesFragmentFromRecyclerView)
        recyclerViewTo = rootView.findViewById(R.id.currenciesFragmentToRecyclerView)
        searchEditTextFrom = rootView.findViewById(R.id.converterFragmentFromEditText)
        searchEditTextTo = rootView.findViewById(R.id.converterFragmentToEditText)
        editTextFromCurrency = rootView.findViewById(R.id.editTextFrom)
        exchangeRateTextView = rootView.findViewById(R.id.converterFragmentExchangeRateTextView)
        exchangeRateTextView.text = getString(R.string.exchange_rate, "0.0")
        exchangeResultTextView = rootView.findViewById(R.id.converterExchangeResultTextView)
        fetchAndSetItems()
        layoutManagerFrom = LinearLayoutManager(context)
        layoutManagerTo = LinearLayoutManager(context)
        recyclerViewFrom.layoutManager = layoutManagerFrom
        recyclerViewTo.layoutManager = layoutManagerTo

        val snapHelperFrom = LinearSnapHelper()
        val snapHelperTo = LinearSnapHelper()
        snapHelperFrom.attachToRecyclerView(recyclerViewFrom)
        snapHelperTo.attachToRecyclerView(recyclerViewTo)

        recyclerViewFrom.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val centerView = snapHelperFrom.findSnapView(layoutManagerFrom)
                    val position = centerView?.let { layoutManagerFrom.getPosition(it) }
                    if (position != lastPositionFrom) {
                        lastPositionFrom = position
                        val fromCurrency = currencyAdapterWheelFrom.items[lastPositionFrom ?: 0].id
                        val toCurrency = currencyAdapterWheelTo.items[lastPositionTo ?: 0].id
                        fetchAndSetExchangeRate(fromCurrency, toCurrency)
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val centerY = (recyclerView.height - recyclerView.paddingTop - recyclerView.paddingBottom) / 2f + recyclerView.paddingTop
                for (i in 0 until recyclerView.childCount) {
                    val child: View = recyclerView.getChildAt(i)
                    val childCenterY = (recyclerView.layoutManager!!.getDecoratedTop(child) + recyclerView.layoutManager!!.getDecoratedBottom(child)) / 2f
                    val scale = 1 - 0.15f * abs(centerY - childCenterY) / centerY
                    child.scaleX = scale
                    child.scaleY = scale
                }
            }
        })
        recyclerViewTo.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val centerView = snapHelperTo.findSnapView(layoutManagerTo)
                    val position = centerView?.let { layoutManagerTo.getPosition(it) }
                    if (position != lastPositionTo) {
                        lastPositionTo = position
                        val fromCurrency = currencyAdapterWheelFrom.items[lastPositionFrom ?: 0].id
                        val toCurrency = currencyAdapterWheelTo.items[lastPositionTo ?: 0].id
                        fetchAndSetExchangeRate(fromCurrency, toCurrency)
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val centerY = (recyclerView.height - recyclerView.paddingTop - recyclerView.paddingBottom) / 2f + recyclerView.paddingTop
                for (i in 0 until recyclerView.childCount) {
                    val child: View = recyclerView.getChildAt(i)
                    val childCenterY = (recyclerView.layoutManager!!.getDecoratedTop(child) + recyclerView.layoutManager!!.getDecoratedBottom(child)) / 2f
                    val scale = 1 - 0.15f * abs(centerY - childCenterY) / centerY
                    child.scaleX = scale
                    child.scaleY = scale
                }
            }
        })

        searchEditTextFrom.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                searchSubjectFrom.onNext(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        searchEditTextTo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                searchSubjectTo.onNext(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        editTextFromCurrency.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val fromCurrencyText = editTextFromCurrency.text?.toString()
                val fromCurrencyValue = if (fromCurrencyText.isNullOrEmpty()) {
                    0.0
                } else {
                    try {
                        fromCurrencyText.toDouble()
                    } catch (e: NumberFormatException) {
                        0.0
                    }
                }

                exchangeResultTextView.text = (exchangeRateValue * fromCurrencyValue).toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        searchSubjectFrom.debounce(300, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String> {
                override fun onSubscribe(d: Disposable) {
                    // handle subscription if needed
                }

                override fun onNext(query: String) {
                    if (query.isNotEmpty()) {
                        findAndScrollToItem(query, recyclerViewFrom, layoutManagerFrom, currencyAdapterWheelFrom)
                    }
                }

                override fun onError(e: Throwable) {
                    // handle error if needed
                }

                override fun onComplete() {
                    // handle completion if needed
                }
            })

        searchSubjectTo.debounce(300, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String> {
                override fun onSubscribe(d: Disposable) {
                    // handle subscription if needed
                }

                override fun onNext(query: String) {
                    if (query.isNotEmpty()) {
                        findAndScrollToItem(query, recyclerViewTo, layoutManagerTo, currencyAdapterWheelTo)
                    }
                }

                override fun onError(e: Throwable) {
                    // handle error if needed
                }

                override fun onComplete() {
                    // handle completion if needed
                }
            })

        return rootView
    }

    private fun findAndScrollToItem(query: String, recyclerView: RecyclerView, layoutManager: LinearLayoutManager, adapter: CurrencyAdapterWheel) {
        val index = adapter.items.indexOfFirst { it.code.lowercase().contains(query.lowercase()) }

        if (index >= 0) {
            layoutManager.smoothScrollToPosition(recyclerView, null, index)
        }
    }

    private fun fetchAndSetItems() {
        dataBase.currencies()
            .getAllSingle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ currencies ->
                val items = currencies.map {
                    CurrencyItem(
                        id = it.id,
                        imageRes = resources.getIdentifier(it.code.lowercase(), "drawable", context?.packageName),
                        code = it.code
                    )
                }
                currencyAdapterWheelFrom = CurrencyAdapterWheel(items)
                currencyAdapterWheelTo = CurrencyAdapterWheel(items)
                recyclerViewFrom.adapter = currencyAdapterWheelFrom
                recyclerViewTo.adapter = currencyAdapterWheelTo
            }, { error ->
                Toast.makeText(requireContext(),error.message,Toast.LENGTH_SHORT)
            }).let { compositeDisposable.add(it) }
    }


    private fun fetchAndSetExchangeRate(fromCurrency: Int, toCurrency: Int) {
        dataBase.currenciesExchange()
            .getExchangeRateSingle(fromCurrency, toCurrency)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ exchangeRate ->
                exchangeRateTextView.text = getString(R.string.exchange_rate, exchangeRate.rate.toString())
                exchangeRateValue = exchangeRate.rate
                       }, { error ->
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
            }).let { compositeDisposable.add(it) }
    }
}