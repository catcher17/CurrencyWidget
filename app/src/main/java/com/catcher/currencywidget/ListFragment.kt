package com.catcher.currencywidget

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListFragment : Fragment() {


    private lateinit var currencyAdapter: ChartCurrencyAdapter
    private lateinit var currencyCodeInput: EditText
    private lateinit var recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_currencies, container, false)

        recyclerView = view.findViewById(R.id.currenciesFragmentRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        currencyCodeInput = view.findViewById(R.id.currencyCodeEditText)
        currencyCodeInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                // Здесь обновите RecyclerView, основываясь на новом тексте в currencyCodeInput.
                updateRecyclerView(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        return view
    }

    private fun updateRecyclerView(currencyCode: String) {
        // Здесь вы можете сделать запрос к базе данных или API, чтобы получить новые данные валюты.
        // Затем вы можете использовать эти данные, чтобы обновить currencyAdapter и применить его к RecyclerView.
    }
}