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
import io.woonex.stockBuddy.Stock
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
        val postRowAdapter = StockRowAdapter(viewModel) {
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
            }
        }

        adapter = postRowAdapter

        //TODO need to fetch from repo for user
        adapter.submitList(listOf(Stock("GOOG"), Stock("F")))


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
}