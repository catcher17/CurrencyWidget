package com.catcher.currencywidget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CurrencyAdapterWheel(internal val items: List<CurrencyItem>) : RecyclerView.Adapter<CurrencyAdapterWheel.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.currencyRecyclerItemImageView)
        val textView: TextView = itemView.findViewById(R.id.currencyRecyclerItemTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.currency_recycler_wheel_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.setImageResource(items[position].imageRes)
        holder.textView.text = items[position].code
    }

    override fun getItemCount() = items.size
}