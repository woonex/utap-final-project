package io.woonex.stockBuddy.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.mikephil.charting.data.Entry
import io.finnhub.api.models.EarningResult
import io.woonex.stockBuddy.AxisValueFormatter
import io.woonex.stockBuddy.LineChartUtils
import io.woonex.stockBuddy.R
import io.woonex.stockBuddy.Stock
import io.woonex.stockBuddy.databinding.FragmentEarningsBinding
import io.woonex.stockBuddy.databinding.FragmentOneStockBinding
import java.time.ZoneId
import java.time.ZoneOffset

class OneStockFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentOneStockBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val args: OneStockFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOneStockBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**Sets a favorite item resource
     *
     */
    private fun setFavorite(isFav: Boolean) {
        val favIcon = if (isFav) {
            R.drawable.ic_favorite_black_24dp
        } else {
            R.drawable.ic_favorite_border_black_24dp
        }
        binding.oneStockFavorite.setImageResource(favIcon)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel: MainViewModel by activityViewModels()

        viewModel.setTitle("One Stock")

        //setup abbreviation
        viewModel.setSingleStockAbbr(args.stockAbbreviation)
        binding.oneStockName.oneStockAbbreviation.text=args.stockAbbreviation

        //deal with favorites
        setFavorite(viewModel.isFavorite(args.stockAbbreviation))
        binding.oneStockFavorite.setOnClickListener {
            val isNowFav = viewModel.flipFavorite(args.stockAbbreviation)
            setFavorite(isNowFav)
        }

        //observe stock full name
        viewModel.observeSingleStockName().observe(viewLifecycleOwner) {
            binding.oneStockName.oneStockName.text = it
        }

        //observe price
        binding.oneStockName.oneStockPrice.text = ""
        viewModel.observeQuote().observe(viewLifecycleOwner) {
            binding.oneStockName.oneStockPrice.text = String.format("%.2f", it.c)
        }

        //observe followers
        viewModel.observeFollowers().observe(viewLifecycleOwner) {
            binding.followersCount.text = it.toString()
        }

        //setup line chart
        binding.lineChart.clear()
        viewModel.observeSingleHistorical().observe(viewLifecycleOwner) {
            val entries = mutableListOf<Entry>()
            //following the approach from here by Yasir-Ghunaim commented on Aug 22, 2016: https://github.com/PhilJay/MPAndroidChart/issues/789

            Log.d("First date:", it[0].date.toString())
            Log.d("last date:", it[it.lastIndex].date.toString())
            val xref: Long = it[0].date.toEpochSecond(ZoneOffset.UTC)
            val axisValueFormatter = AxisValueFormatter(xref, false)

            for (timeData in it) {
                entries.add(Entry((timeData.date.toEpochSecond(ZoneOffset.UTC) - xref).toFloat(), timeData.stockPrice.closePrice.toFloat()))
            }

            LineChartUtils.setupLineChart(binding.lineChart, entries, "Titles", axisValueFormatter)
        }

        clearEarnings()
        viewModel.observeEarnings().observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                clearEarnings()

            } else {
                setEarning(it[0], binding.earningData1)
                setEarning(it[1], binding.earningData2)
                setEarning(it[2], binding.earningData3)
                setEarning(it[3], binding.earningData4)
            }
        }

        //observer recommendations
        clearRecommendation()
        viewModel.observeRecommendation().observe(viewLifecycleOwner) { recommendationTrend ->
            val hardSell = recommendationTrend.strongSell ?:0
            val sell = recommendationTrend.sell ?:0
            val hold = recommendationTrend.hold ?:0
            val buy = recommendationTrend.buy?:0
            val hardBuy = recommendationTrend.strongBuy?:0
            val date = recommendationTrend.period

            val total = hardSell + sell + hold + buy + hardBuy + 0.000_001
            binding.hardSell.text = String.format("%.1f%%", hardSell/total * 100.0)
            binding.sell.text = String.format("%.1f%%", sell/total * 100.0)
            binding.hold.text = String.format("%.1f%%", hold/total * 100.0)
            binding.buy.text = String.format("%.1f%%", buy/total * 100.0)
            binding.hardBuy.text = String.format("%.1f%%", hardBuy/total * 100.0)
            binding.recommendationDate.text = date
        }

        //observe similar companies
        viewModel.observeSimilar().observe(viewLifecycleOwner) {
            setRelated(it)
        }
        setRelated()
    }

    override fun onStart() {
        super.onStart()
        viewModel.showSearchBarNav()
        viewModel.showActionBarFavorites()

        viewModel.getFavorite()?.setOnClickListener {
            val navController = findNavController()
            val action = OneStockFragmentDirections.actionOneStockFragmentToHomeFragment()
            navController.navigate(action)
        }

        viewModel.getSearch()?.setOnClickListener {
            val navController = findNavController()
            val action = OneStockFragmentDirections.actionOneStockFragmentToSearchFragment()
            navController.navigate(action)
        }
    }

    private fun clearEarnings() {
        val blank = EarningResult(0f, 0f, 0f, 0f, "", "", 0)
        setEarning(blank, binding.earningData1)
        setEarning(blank, binding.earningData2)
        setEarning(blank, binding.earningData3)
        setEarning(blank, binding.earningData4)
    }

    private fun setEarning(earningResult: EarningResult, earningData: FragmentEarningsBinding) {
        earningData.date.text = earningResult.period
        earningData.actual.text = String.format("%.2f",earningResult.actual)
        earningData.estimate.text = String.format("%.2f",earningResult.estimate)
        earningData.surprisePercent.text = String.format("%.2f",earningResult.surprisePercent)
        val color = if (earningResult.surprisePercent!! >= 0f) Color.GREEN else Color.RED
        earningData.surprisePercent.setTextColor(color)
    }

    private fun clearRecommendation() {
        binding.hardSell.text = ""
        binding.sell.text = ""
        binding.hold.text = ""
        binding.buy.text = ""
        binding.hardBuy.text = ""
        binding.recommendationDate.text = ""
    }

    /** Sets the related stocks to the current ones
     *
     */
    private fun setRelated(stocks: List<Stock> = listOf(Stock(""), Stock(""), Stock(""))) {
        var i = 1
        for (stock in stocks) {
            val field = when (i) {
                1 -> binding.relatedStock1
                2 -> binding.relatedStock2
                3 -> binding.relatedStock3
                else -> null
            }
            i++

            if (stock.name == "") {
                field?.root?.visibility = View.GONE
                continue
            } else {
                field?.root?.visibility = View.VISIBLE
            }
            field?.oneStockName?.text = stock.name
            field?.oneStockAbbreviation?.text = stock.abbreviation


            field?.oneStockPrice?.text = if (stock.currentPrice == 0f || stock.currentPrice == null) ""
                else String.format("%.2f", stock.currentPrice)

            field?.root?.setOnClickListener {
                viewModel.clearSimilar()
                //todo add here
                val navController = findNavController()
                val action = OneStockFragmentDirections.actionOneStockFragmentToOneStockFragment(stock.abbreviation)
                navController.navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}