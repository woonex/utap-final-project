package io.woonex.stockBuddy.ui

import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.Entry
import io.woonex.stockBuddy.LineChartUtils
import io.woonex.stockBuddy.databinding.RowStockBinding
import io.woonex.stockBuddy.Stock
import java.util.Random

/**
 * Created by witchel on 8/25/2019 adapted by woonex on 04/07/2024
 */

// https://developer.android.com/reference/androidx/recyclerview/widget/ListAdapter
// Slick adapter that provides submitList, so you don't worry about how to update
// the list, you just submit a new one when you want to change the list and the
// Diff class computes the smallest set of changes that need to happen.
// NB: Both the old and new lists must both be in memory at the same time.
// So you can copy the old list, change it into a new list, then submit the new list.
//
// You can call adapterPosition to get the index of the selected item
class StockRowAdapter(private val viewModel: MainViewModel,
                     private val navigateToOneStock: (Stock)->Unit )
    : ListAdapter<Stock, StockRowAdapter.VH>(StockDiff()) {

    inner class VH(val rowPostBinding: RowStockBinding)
        : RecyclerView.ViewHolder(rowPostBinding.root)

    class StockDiff : DiffUtil.ItemCallback<Stock>() {
        override fun areItemsTheSame(oldItem: Stock, newItem: Stock): Boolean {
            return oldItem.abbreviation == newItem.abbreviation
        }
        override fun areContentsTheSame(oldItem: Stock, newItem: Stock): Boolean {
            return oldItem.abbreviation == newItem.abbreviation
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val rowBinding = RowStockBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return VH(rowBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        val rowBinding = holder.rowPostBinding
        rowBinding.abbreviation.text = item.abbreviation
        rowBinding.currentPrice.text = item.currentPrice.toString()


        if (item.open == null) {
            rowBinding.openPrice.visibility = View.INVISIBLE
            rowBinding.openText.visibility = View.INVISIBLE
        } else {
            rowBinding.openPrice.visibility = View.VISIBLE
            rowBinding.openText.visibility = View.VISIBLE
        }
        rowBinding.openPrice.text = item.open.toString()

        if (item.low == null) {
            rowBinding.lowPrice.visibility = View.INVISIBLE
            rowBinding.lowText.visibility = View.INVISIBLE
        } else {
            rowBinding.lowPrice.visibility = View.VISIBLE
            rowBinding.lowText.visibility = View.VISIBLE
        }
        rowBinding.lowPrice.text = item.low.toString()

        if (item.high == null) {
            rowBinding.highPrice.visibility = View.INVISIBLE
            rowBinding.highText.visibility = View.INVISIBLE
        } else {
            rowBinding.highPrice.visibility = View.VISIBLE
            rowBinding.highText.visibility = View.VISIBLE
        }
        rowBinding.highPrice.text = item.high.toString()


        if (item.volume == null) {
            rowBinding.volume.visibility = View.INVISIBLE
            rowBinding.volumeText.visibility = View.INVISIBLE
        } else {
            rowBinding.volume.visibility = View.VISIBLE
            rowBinding.volumeText.visibility = View.VISIBLE
        }
        rowBinding.volume.text = item.volume.toString()

        val random = Random()

        val entries = mutableListOf<Entry>()

        var lastEntry = 50f;
        for (i in 0 until 100) {
            val sign = if (random.nextBoolean()) 1 else -1
            lastEntry += random.nextFloat() * 5 * sign
            entries.add(Entry(i.toFloat(), lastEntry))
        }

        LineChartUtils.setupLineChart(rowBinding.lineChart, entries)
        rowBinding.root.setOnClickListener {
            navigateToOneStock(item)
        }
    }
}

