package io.woonex.stockBuddy.api

import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.infrastructure.ApiClient

interface FinnhubApi {
    companion object {
        fun create(): DefaultApi {
            ApiClient.apiKey["token"] = "coc94jpr01qj8q79ptugcoc94jpr01qj8q79ptv0"
            return DefaultApi()
        }
    }
}