package io.woonex.stockBuddy.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import io.woonex.stockBuddy.databinding.FragmentOneStockBinding

class OneStockFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentOneStockBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val args: OneStockFragmentArgs by navArgs()

//    private lateinit var

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOneStockBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel: MainViewModel by activityViewModels()
        viewModel.hideActionBarFavorites()

        viewModel.setTitle("One Stock")

        //setup abbreviation
        viewModel.setSingleStockAbbr(args.stockAbbreviation)
        binding.oneStockName.oneStockAbbreviation.text=args.stockAbbreviation

        //observe stock full name
        viewModel.observeSingleStockName().observe(viewLifecycleOwner) {
            binding.oneStockName.oneStockName.text = it
        }

        //observe price
        viewModel.observeQuote().observe(viewLifecycleOwner) {
            binding.oneStockName.oneStockPrice.text = it.currentPrice.toString()
        }

        //observer recommendations
        viewModel.observeRecommendation().observe(viewLifecycleOwner) {
            val hardSell = it.strongSell!!
            val sell = it.sell!!
            val hold = it.hold!!
            val buy = it.buy!!
            val hardBuy = it.strongBuy!!
            val date = it.period


            val total = hardSell + sell + hold + buy + hardBuy
            binding.hardSell.text = String.format("%.1f%%", hardSell/total.toDouble() * 100.0)
            binding.sell.text = String.format("%.1f%%", sell/total.toDouble() * 100.0)
            binding.hold.text = String.format("%.1f%%", hold/total.toDouble() * 100.0)
            binding.buy.text = String.format("%.1f%%", buy/total.toDouble() * 100.0)
            binding.hardBuy.text = String.format("%.1f%%", hardBuy/total.toDouble() * 100.0)
            binding.recommendationDate.text = date
        }

        //observe similar companies
        viewModel.observeSimilar().observe(viewLifecycleOwner) {
            var i = 1
            for (stock in it) {
                val field = when (i) {
                    1 -> binding.relatedStock1
                    2 -> binding.relatedStock2
                    3 -> binding.relatedStock3
                    else -> null
                }

                field?.oneStockName?.text = stock.name
                field?.oneStockAbbreviation?.text = stock.abbreviation
                field?.oneStockPrice?.text = String.format("%.2f", stock.currentPrice)

                i++
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}