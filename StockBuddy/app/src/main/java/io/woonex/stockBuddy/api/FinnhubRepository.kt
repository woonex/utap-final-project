package io.woonex.stockBuddy.api

import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.models.EarningResult
import io.finnhub.api.models.Quote
import io.finnhub.api.models.RecommendationTrend
import io.finnhub.api.models.SymbolLookup
import io.finnhub.api.models.SymbolLookupInfo

class FinnhubRepository(private val finnhubApi: DefaultApi) {
    suspend fun getQuote(symbol: String): Quote {
        return try {
            finnhubApi.quote(symbol)
        } catch (e : Exception) {
            Quote()
        }
    }

    suspend fun getRecommendation(symbol:String): RecommendationTrend {
        try {
            val allData = finnhubApi.recommendationTrends(symbol)
            if (allData.isEmpty()) {
                return RecommendationTrend()
            }
            return allData[0]
        } catch (e : Exception) {
            return RecommendationTrend()
        }

    }

    suspend fun getSimilar(symbol:String): List<String> {
        return try {
            return finnhubApi.companyPeers(symbol, "subIndustry")
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getName(symbol:String) :String {
        val search = finnhubApi.symbolSearch(symbol)
        return search.result?.get(0)?.description!!
    }

    suspend fun getEarningsReport(symbol: String): List<EarningResult> {
        return try {
            finnhubApi.companyEarnings(symbol, limit = 10)
        } catch (e : Exception) {
            emptyList()
        }
    }

    suspend fun searchForSymbol(query : String) : List<SymbolLookupInfo> {
        if (query == "") {
            return emptyList()
        }

        return finnhubApi.symbolSearch(query).result!!
    }
}