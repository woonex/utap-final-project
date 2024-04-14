package io.woonex.stockBuddy.ui


import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import io.finnhub.api.models.EarningResult
import io.finnhub.api.models.EarningsCalendar
import io.finnhub.api.models.RecommendationTrend
import io.finnhub.api.models.SymbolLookupInfo
import io.woonex.stockBuddy.Quote
import io.woonex.stockBuddy.R
import io.woonex.stockBuddy.Stock
import io.woonex.stockBuddy.TimeScope
import io.woonex.stockBuddy.alpha.TimeData
import io.woonex.stockBuddy.api.AlphaApi
import io.woonex.stockBuddy.api.AlphaRepository
import io.woonex.stockBuddy.api.FinnhubApi
import io.woonex.stockBuddy.api.FinnhubRepository
//import io.woonex.stockBuddy.api.RedditApi
//import io.woonex.stockBuddy.api.RedditPost
//import io.woonex.stockBuddy.api.RedditPostRepository
import io.woonex.stockBuddy.databinding.ActionBarBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private var actionBarBinding : ActionBarBinding? = null
    private val finnhubApi = FinnhubApi.create()
    private val finnhubRepo = FinnhubRepository(finnhubApi)

    private val alphaApi = AlphaApi.create()
    private val alphaRepo = AlphaRepository(alphaApi)

    private var title = MutableLiveData<String>()
    fun observeTitle(): LiveData<String> {
        return title
    }
    fun setTitle(newTitle: String) {
        title.value = newTitle
    }

    private var displayFavorites = MutableLiveData<List<Stock>>().apply {
        value = mutableListOf()
    }

    fun observeDisplayFavorites() : LiveData<List<Stock>> {
        return displayFavorites
    }

    private var favNames = MutableLiveData<List<String>>().apply {
        value = mutableListOf()
    }

    fun isFavorite(abbr: String): Boolean {
        Log.d("ViewModel", favNames.value.toString())
        return favNames.value?.contains(abbr)!!
    }

    fun flipFavorite(abbr: String) : Boolean {
        val initialList : MutableList<String> = favNames.value?.toMutableList() ?: return false
        val data = if (isFavorite(abbr)) {
            Log.d("ViewModel", "Removing favorite")
            initialList.remove(abbr)
            false
        } else {
            Log.d("ViewModel", "Adding favorite")
            initialList.add(abbr)
            true
        }
        favNames.postValue(initialList)
        return data
    }

    fun buildFavorite(abbr : String) :Stock {
        //TODO do more here
        return Stock(abbr)
    }

    private var singleStockAbbr = MutableLiveData<String>().apply{
        value = ""
    }

    private var singleStockName = MediatorLiveData<String>().apply{
        addSource(singleStockAbbr) {singleStockAbbr ->
            viewModelScope.launch(
                context = viewModelScope.coroutineContext
                        + Dispatchers.IO) {
                val name = finnhubRepo.getName(singleStockAbbr)
                postValue(name)
            }
        }
    }

    fun observeSingleStockName() : LiveData<String> {
        return singleStockName
    }

    /**Sets the value for the single stock abbreviation the user is observing
     *
     */
    fun setSingleStockAbbr(newSingleStock: String) {
        singleStockAbbr.value = newSingleStock
    }

    private var timeScope = MutableLiveData<TimeScope>().apply{
        value = TimeScope.WEEKLY
    }

    fun setTimeScope(timeScope : TimeScope) {
        this.timeScope.postValue(timeScope)
    }

    private val singleTimeData = MediatorLiveData<List<TimeData>>().apply {
        addSource(singleStockAbbr) {
            viewModelScope.launch(
                context = viewModelScope.coroutineContext
                        + Dispatchers.IO) {
                postValue(alphaRepo.getDaily(singleStockAbbr.value!!))
            }
        }
    }

    fun observeSingleHistorical() : LiveData<List<TimeData>> {
        return singleTimeData
    }

    private var quote = MediatorLiveData<Quote>().apply{
        addSource(singleStockAbbr) { singleStockAbbr ->
            Log.d("singleStockAbbr", singleStockAbbr)
            viewModelScope.launch(
                context = viewModelScope.coroutineContext
                        + Dispatchers.IO) {
                val quote = finnhubRepo.getQuote(singleStockAbbr)
                postValue(quote)
            }
        }
    }

    fun observeQuote() : LiveData<Quote> {
        return quote
    }

    private var earnings = MediatorLiveData<List<EarningResult>>().apply{
        addSource(singleStockAbbr) {
            viewModelScope.launch(
                context = viewModelScope.coroutineContext
                        + Dispatchers.IO) {
                val earnings = finnhubRepo.getEarningsReport(it)
                postValue(earnings)
            }
        }
    }

    fun observeEarnings() : LiveData<List<EarningResult>> {
        return earnings
    }

    private var recommendation = MediatorLiveData<RecommendationTrend>().apply{
        addSource(singleStockAbbr) { singleStockAbbr ->
            Log.d("singleStock", singleStockAbbr)
            viewModelScope.launch(
                context = viewModelScope.coroutineContext
                        + Dispatchers.IO) {
                val recommend = finnhubRepo.getRecommendation(singleStockAbbr)
                postValue(recommend)
            }
        }
    }

    fun observeRecommendation() : LiveData<RecommendationTrend> {
        return recommendation
    }

    private var similar = MediatorLiveData<List<Stock>>().apply{
        addSource(singleStockAbbr) {currentStockAbbr ->
            postValue(emptyList())
            viewModelScope.launch(
                context = viewModelScope.coroutineContext
                        + Dispatchers.IO) {
                val allRecommend = finnhubRepo.getSimilar(currentStockAbbr).toMutableList()
                allRecommend.remove(currentStockAbbr)

                val fullStocks = mutableListOf<Stock>()
                var i = 0;
                for (abbr in allRecommend) {
                    val name = finnhubRepo.getName(abbr)
                    val price = finnhubRepo.getQuote(abbr).currentPrice
                    if (price != 0f) {
                        fullStocks.add(Stock(abbr, name, price))
                        i++
                    }
                    if (i >= 3) {
                        break;
                    }
                }
                postValue(fullStocks)
            }
        }
    }

    fun observeSimilar() : LiveData<List<Stock>> {
        return similar
    }

    fun setSearchTerm(query : String) {
        favoritesSearch.postValue(query)
    }

    private var favoritesSearch = MutableLiveData<String>().apply {
        value = ""
    }

    private var searchResults = MediatorLiveData<List<SymbolLookupInfo>>().apply {
        addSource(favoritesSearch) {
            viewModelScope.launch(
                context = viewModelScope.coroutineContext
                        + Dispatchers.IO) {
                val recommend = finnhubRepo.searchForSymbol(it)
                postValue(recommend)
            }
        }

    }

    fun observeSearchResults() : LiveData<List<SymbolLookupInfo>> {
        return searchResults
    }

    /////////////////////////
    // Action bar
    fun initActionBarBinding(it: ActionBarBinding) {
        actionBarBinding = it
    }
    fun hideActionBarFavorites() {
        actionBarBinding?.actionFavorite?.visibility = View.GONE
    }
    fun showActionBarFavorites() {
        actionBarBinding?.actionFavorite?.visibility = View.VISIBLE
    }

    fun getFavorite(): ImageView? {
        return actionBarBinding?.actionFavorite
    }

    fun getSearch() :ImageView? {
        return actionBarBinding?.search
    }

    fun hideSearchBarNav() {
        actionBarBinding?.search?.visibility = View.GONE
    }

    fun showSearchBarNav() {
        actionBarBinding?.search?.visibility = View.VISIBLE
    }
}