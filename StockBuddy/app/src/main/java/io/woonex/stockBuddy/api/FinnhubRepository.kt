package io.woonex.stockBuddy.api

import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.models.EarningResult
import io.finnhub.api.models.RecommendationTrend
import io.woonex.stockBuddy.Quote

class FinnhubRepository(private val finnhubApi: DefaultApi) {
    suspend fun getQuote(symbol: String): Quote {
        val stuff= finnhubApi.quote(symbol)
        return Quote(stuff.c!!, stuff.h!!, stuff.l!!, stuff.o!!, stuff.pc!!)
    }

    suspend fun getRecommendation(symbol:String): RecommendationTrend {
        val allData = finnhubApi.recommendationTrends(symbol)
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
}