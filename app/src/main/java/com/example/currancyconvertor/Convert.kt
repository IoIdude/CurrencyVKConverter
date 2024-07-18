package com.example.currancyconvertor

import com.example.currancyconvertor.models.CurrencyData
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class Convert() {
    private suspend fun <T> fetchData(process: (CurrencyData) -> T): T {
        val client = OkHttpClient()
        val request = Request.Builder().url("https://www.cbr-xml-daily.ru/daily_json.js").build()

        return suspendCancellableCoroutine { coroutine ->
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            throw IOException("Unexpected code $response")
                        }

                        response.body?.string()?.let { json ->
                            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                            val jsonAdapter = moshi.adapter(CurrencyData::class.java)

                            val data = jsonAdapter.fromJson(json)
                            if (data != null) {
                                coroutine.resume(process(data))
                            }
                        }
                    }
                }
            })
        }
    }

    suspend fun getCurrencyData(selectedCurrency: String, nominal: Int): Float {
        return fetchData() { data ->
            nominal * (data.valute[selectedCurrency]?.value ?: throw IllegalAccessException("Invalid selected currency"))
        }
    }

    suspend fun getCurrencyNames(): List<String> {
        return fetchData() { data ->
            data.valute.keys.toList()
        }
    }
}