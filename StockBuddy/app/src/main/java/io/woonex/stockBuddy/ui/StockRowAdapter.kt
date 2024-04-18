package io.woonex.stockBuddy.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.Entry
import io.woonex.stockBuddy.AxisValueFormatter
import io.woonex.stockBuddy.LineChartUtils
import io.woonex.stockBuddy.databinding.RowStockBinding
import io.woonex.stockBuddy.Stock
import io.woonex.stockBuddy.TimeScope
import java.time.LocalDate
import java.time.ZoneOffset
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
class StockRowAdapter(private val lifecycleOwner: LifecycleOwner,
                      private val viewModel: MainViewModel,
                     private val navigateToOneStock: (Stock)->Unit,
                    )
    : ListAdapter<Stock, StockRowAdapter.VH>(StockDiff()) {

        companion object {
            private fun formatDecimal(data : Float?) :String {
                return String.format("%.2f", data)
            }

        }

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
        rowBinding.currentPrice.text = formatDecimal(item.currentPrice)

        //show or hide the prices if it's open
        if (item.open == null) {
            rowBinding.openPrice.visibility = View.INVISIBLE
            rowBinding.openText.visibility = View.INVISIBLE
        } else {
            rowBinding.openPrice.visibility = View.VISIBLE
            rowBinding.openText.visibility = View.VISIBLE
        }
        rowBinding.openPrice.text = formatDecimal(item.open)

        if (item.low == null) {
            rowBinding.lowPrice.visibility = View.INVISIBLE
            rowBinding.lowText.visibility = View.INVISIBLE
        } else {
            rowBinding.lowPrice.visibility = View.VISIBLE
            rowBinding.lowText.visibility = View.VISIBLE
        }
        rowBinding.lowPrice.text = formatDecimal(item.low)

        if (item.high == null) {
            rowBinding.highPrice.visibility = View.INVISIBLE
            rowBinding.highText.visibility = View.INVISIBLE
        } else {
            rowBinding.highPrice.visibility = View.VISIBLE
            rowBinding.highText.visibility = View.VISIBLE
        }
        rowBinding.highPrice.text = formatDecimal(item.high)


        if (item.change == null) {
            rowBinding.change.visibility = View.INVISIBLE
            rowBinding.changeText.visibility = View.INVISIBLE
        } else {
            rowBinding.change.visibility = View.VISIBLE
            rowBinding.changeText.visibility = View.VISIBLE
        }
        val color = when (item.change) {
            null -> Color.BLACK
            else -> if (item.change >= 0) {
                Color.GREEN
            } else {
                Color.RED
            }
        }
        rowBinding.change.setTextColor(color)
        rowBinding.change.text = formatDecimal(item.change)


        rowBinding.root.setOnClickListener {
            navigateToOneStock(item)
        }

        //setup line chart
        viewModel.observeTimeScope().observe(lifecycleOwner) {timeScope ->
            val filterDate = when (timeScope) {
                TimeScope.WEEKLY -> LocalDate.now().minusDays(7)
                TimeScope.MONTHLY -> LocalDate.now().minusMonths(1)
                TimeScope.ONE_YEAR -> LocalDate.now().minusYears(1)
                TimeScope.FIVE_YEAR -> LocalDate.now().minusYears(5)
                TimeScope.COMPLETE -> LocalDate.now().minusYears(1000)
                else -> LocalDate.now().minusYears(1000)
            }


            val copy = item.historicalData?.filter {
                it.date.isAfter(filterDate.atStartOfDay())
            }

            val xref: Long = copy?.get(0)?.date?.toEpochSecond(ZoneOffset.UTC) ?: 0
            val entries = mutableListOf<Entry>()
            if (copy != null) {
                for (date in copy) {
                    entries.add(Entry((date.date.toEpochSecond(ZoneOffset.UTC) - xref).toFloat(), date.stockPrice.closePrice.toFloat()))
                }
            }

            val axisValueFormatter = AxisValueFormatter(xref, false)
            LineChartUtils.setupLineChart(rowBinding.lineChart, entries, timeScope.toString(), axisValueFormatter)
        }
    }
}

