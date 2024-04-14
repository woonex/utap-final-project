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
import io.woonex.stockBuddy.databinding.SearchRvBinding

class SearchFragment: Fragment() {
    private val viewModel: MainViewModel by activityViewModels()

    private var _binding: SearchRvBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private lateinit var adapter :SearchRowAdapter

    // Set up the adapter and recycler view
    private fun initAdapter(binding: SearchRvBinding) {
        val postRowAdapter = SearchRowAdapter(viewModel) {
            val navController = findNavController()
            val action = SearchFragmentDirections.actionSearchToOneStockFragment(it.abbreviation)
            navController.navigate(action)
        }

//        viewModel.repoFetch()
        binding.recyclerView.adapter = postRowAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)

        binding.searchGo.setOnClickListener {
            //TODO get the data from the search
            //TODO display the search
            viewModel.setSearchTerm(binding.actionSearch.text.toString())
            val main = context as MainActivity
            main.hideKeyboard()
        }

        viewModel.observeSearchResults().observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }


        adapter = postRowAdapter

        //TODO need to fetch from repo for user
        adapter.submitList(listOf())


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SearchRvBinding.inflate(inflater, container, false)
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
        viewModel.hideSearchBarNav()
        viewModel.showActionBarFavorites()
        viewModel.getFavorite()?.setOnClickListener {
            val navController = findNavController()
            val action = SearchFragmentDirections.actionSearchToHomeFragment()
            navController.navigate(action)
        }
    }
}