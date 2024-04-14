package io.woonex.stockBuddy.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import io.finnhub.api.models.SymbolLookupInfo
import io.woonex.stockBuddy.Stock
import io.woonex.stockBuddy.databinding.RowSearchBinding

class SearchRowAdapter(private val viewModel: MainViewModel,
    private val navigateToOneStock: (Stock) -> Unit)
    : ListAdapter<SymbolLookupInfo, SearchRowAdapter.VH>(LookupDiff()) {

    inner class VH (val searchBinding: RowSearchBinding)
        : RecyclerView.ViewHolder(searchBinding.root)

    class LookupDiff : DiffUtil.ItemCallback<SymbolLookupInfo>() {
        override fun areItemsTheSame(oldItem: SymbolLookupInfo, newItem: SymbolLookupInfo): Boolean {
            return oldItem.symbol == newItem.symbol
        }
        override fun areContentsTheSame(oldItem: SymbolLookupInfo, newItem: SymbolLookupInfo): Boolean {
            return oldItem.symbol == newItem.symbol
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchRowAdapter.VH {
        val rowBinding = RowSearchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        return VH(rowBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        //TODO("Not yet implemented")
        val item = getItem(position)
        val binding = holder.searchBinding

        binding.description.text = item.description
        binding.symbol.text = item.symbol
        binding.type.text = item.type
        binding.displaySymbol.text = item.displaySymbol

        binding.root.setOnClickListener {
            val abbr = item.symbol!!
            viewModel.setSingleStockAbbr(abbr)
            navigateToOneStock(Stock(abbr))
        }
    }
}