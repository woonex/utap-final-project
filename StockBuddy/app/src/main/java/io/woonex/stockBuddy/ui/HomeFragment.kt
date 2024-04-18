package io.woonex.stockBuddy.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.woonex.stockBuddy.MainActivity
import io.woonex.stockBuddy.R
import io.woonex.stockBuddy.SortOrder
import io.woonex.stockBuddy.Stock
import io.woonex.stockBuddy.TimeScope
import io.woonex.stockBuddy.databinding.FragmentRvBinding

class HomeFragment: Fragment() {
    private val viewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentRvBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private lateinit var adapter :StockRowAdapter

    companion object {
        fun getFilteredList(searchTerm: String, original: List<Stock>) :List<Stock> {
            val filteredList = mutableListOf<Stock>()
            for (current in original) {
                val isFound: Boolean = current.searchFor(searchTerm)
                if (isFound) {
                    filteredList.add(current)
                }
            }

            return filteredList
        }
    }

    // Set up the adapter and recycler view
    private fun initAdapter(binding: FragmentRvBinding) {
        val postRowAdapter = StockRowAdapter(viewLifecycleOwner, viewModel) {
            val navController = findNavController()
            val action = HomeFragmentDirections.actionHomeFragmentToOneStockFragment(it.abbreviation)
            navController.navigate(action)
        }


//        viewModel.repoFetch()
        binding.recyclerView.adapter = postRowAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)



        binding.durationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("woonex", "World")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.d("woonex", resources.getStringArray(R.array.duration_choices)[position])
                val choice = resources.getStringArray(R.array.duration_choices)[position]
                if (choice.equals("week", true)) {
                    viewModel.setTimeScope(TimeScope.WEEKLY)
                } else if (choice.equals("month", true)) {
                    viewModel.setTimeScope(TimeScope.MONTHLY)
                } else if (choice.equals("1 year", true)) {
                    viewModel.setTimeScope(TimeScope.ONE_YEAR)
                } else if (choice.equals("5 year", true)) {
                    viewModel.setTimeScope(TimeScope.FIVE_YEAR)
                } else {
                    viewModel.setTimeScope(TimeScope.COMPLETE)
                }
            }
        }

        binding.sortOrderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val choice = resources.getStringArray(R.array.sort_order_choices)[position]
                if (choice.equals("low price", true)) {
                    viewModel.setSortOrder(SortOrder.LOW_PRICE)
                } else if (choice.equals("high price", true)) {
                    viewModel.setSortOrder(SortOrder.HIGH_PRICE)
                } else if (choice.equals("alphabetical", true)) {
                    viewModel.setSortOrder(SortOrder.ALPHABETICAL)
                } else {
                    viewModel.setSortOrder(SortOrder.REVERSE_ALPHABETICAL)
                }
            }
        }

        adapter = postRowAdapter

        binding.loadingFavorites.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        binding.noFavoritesNotice.visibility = View.GONE
        viewModel.observeDisplayFavorites().observe(viewLifecycleOwner) {
            handleRvListSubmission(it)
        }

        viewModel.observeSortOrder().observe(viewLifecycleOwner) {sortOrder ->
            val copy = viewModel.observeDisplayFavorites().value
            Log.d("HomeFragment", "Original order: $copy" + copy)
            val sortedCopy = when (sortOrder) {
                SortOrder.LOW_PRICE -> copy?.sortedBy { it.currentPrice }
                SortOrder.HIGH_PRICE -> copy?.sortedByDescending { it.currentPrice }
                SortOrder.ALPHABETICAL -> copy?.sortedBy { it.abbreviation }
                SortOrder.REVERSE_ALPHABETICAL -> copy?.sortedByDescending { it.abbreviation }
            }

            Log.d("HomeFragment", "Sorted order: $sortedCopy")
            if (sortedCopy != null) {
                adapter.submitList(sortedCopy)
            }
        }
    }

    private fun handleRvListSubmission(stocks : List<Stock>) {
        if (!viewModel.getIsInitialized()) {
            return
        }
        if (stocks.isEmpty()) {
            binding.recyclerView.visibility = View.GONE
            binding.noFavoritesNotice.visibility = View.VISIBLE
            binding.loadingFavorites.visibility = View.GONE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.noFavoritesNotice.visibility = View.GONE
            binding.loadingFavorites.visibility = View.GONE
        }
        adapter.submitList(stocks)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRvBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")
        // XXX Write me.  Set title based on current subreddit
        initAdapter(this.binding)

        val title = "Stocks"
        viewModel.setTitle(title)
    }

    override fun onStart() {
        super.onStart()
        viewModel.hideActionBarFavorites()
        viewModel.showSearchBarNav()
        viewModel.getSearch()?.setOnClickListener {
            val navController = findNavController()
            val action = HomeFragmentDirections.actionHomeFragmentToSearch()
            navController.navigate(action)
        }
    }
}