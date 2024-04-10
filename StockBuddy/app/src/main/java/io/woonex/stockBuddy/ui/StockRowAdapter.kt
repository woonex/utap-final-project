package io.woonex.stockBuddy.ui

import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.woonex.stockBuddy.R
import io.woonex.stockBuddy.databinding.RowStockBinding
import io.woonex.stockBuddy.Stock

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
                     private val navigateToOnePost: (Stock)->Unit )
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
        viewModel.showActionBarFavorites()
        val rowBinding = RowStockBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return VH(rowBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        val rowBinding = holder.rowPostBinding
        rowBinding.name.text = item.name
        rowBinding.abbreviation.text = item.abbreviation
    }
}

