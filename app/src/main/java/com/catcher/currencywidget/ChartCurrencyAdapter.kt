package com.catcher.currencywidget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData


class ChartCurrencyAdapter(private val items: List<CurrencyItem>) : RecyclerView.Adapter<ChartCurrencyAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.flagImageView)
        val textView: TextView = itemView.findViewById(R.id.customViewCodeTextView)
        val lineChart: LineChart = itemView.findViewById(R.id.chart)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.currency_chart_recycler_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.setImageResource(items[position].imageRes)
        holder.textView.text = items[position].code
        holder.lineChart.data = generateChartData(items[position])
    }

    private fun generateChartData(CurrencyItem: CurrencyItem): LineData {
        // Здесь вы можете сгенерировать данные для графика, основанные на информации о валюте.
        return
    }

    override fun getItemCount() = items.size
}