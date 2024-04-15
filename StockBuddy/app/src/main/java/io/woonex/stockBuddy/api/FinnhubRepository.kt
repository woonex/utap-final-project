package io.woonex.stockBuddy.api

import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.models.EarningResult
import io.finnhub.api.models.Quote
import io.finnhub.api.models.RecommendationTrend
import io.finnhub.api.models.SymbolLookup
import io.finnhub.api.models.SymbolLookupInfo

class FinnhubRepository(private val finnhubApi: DefaultApi) {
    suspend fun getQuote(symbol: String): Quote {
        return finnhubApi.quote(symbol)
    }

    suspend fun getRecommendation(symbol:String): RecommendationTrend {
        val allData = finnhubApi.recommendationTrends(symbol)
        if (allData.isEmpty()) {
            return RecommendationTrend(buy=0, hold=0, sell=0, strongBuy = 0, strongSell = 0)
        }
        return allData[0]
    }

    suspend fun getSimilar(symbol:String): List<String> {
        return finnhubApi.companyPeers(symbol, "subIndustry")
    }

    suspend fun getName(symbol:String) :String {
        val search = finnhubApi.symbolSearch(symbol)
        return search.result?.get(0)?.description!!
    }

    suspend fun getEarningsReport(symbol: String): List<EarningResult> {
        return finnhubApi.companyEarnings(symbol, limit = 10)
    }

    suspend fun searchForSymbol(query : String) : List<SymbolLookupInfo> {
        if (query == "") {
            return emptyList()
        }

        return finnhubApi.symbolSearch(query).result!!
    }
}