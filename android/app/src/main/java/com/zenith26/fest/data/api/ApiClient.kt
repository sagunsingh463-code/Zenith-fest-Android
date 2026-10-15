package com.zenith26.fest.data.api

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.zenith26.fest.data.model.ApiErrorResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton managing Retrofit, OkHttp, and dynamic Base URL configuration.
 *
 * For Android Emulator: 10.0.2.2 points to host machine's localhost (FastAPI port 8000).
 * For physical devices or other environments: user can adjust in the in-app settings modal.
 */
object ApiClient {

    private const val PREFS_NAME = "zenith26_network_prefs"
    private const val KEY_BASE_URL = "api_base_url"

    // Default emulator loopback address to host machine
    const val DEFAULT_EMULATOR_BASE_URL = "http://10.0.2.2:8081/"
    const val LOCALHOST_BASE_URL = "http://127.0.0.1:8081/"

    private var prefs: SharedPreferences? = null
    private var currentBaseUrl: String = DEFAULT_EMULATOR_BASE_URL

    val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    private var retrofit: Retrofit? = null
    private var apiService: ZenithApiService? = null

    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        currentBaseUrl = prefs?.getString(KEY_BASE_URL, DEFAULT_EMULATOR_BASE_URL) ?: DEFAULT_EMULATOR_BASE_URL
        if (!currentBaseUrl.endsWith("/")) {
            currentBaseUrl += "/"
        }
        buildRetrofit()
    }

    fun getBaseUrl(): String = currentBaseUrl

    fun updateBaseUrl(newUrl: String) {
        var formatted = newUrl.trim()
        if (!formatted.startsWith("http://") && !formatted.startsWith("https://")) {
            formatted = "http://$formatted"
        }
        if (!formatted.endsWith("/")) {
            formatted += "/"
        }
        currentBaseUrl = formatted
        prefs?.edit()?.putString(KEY_BASE_URL, currentBaseUrl)?.apply()
        buildRetrofit()
    }

    private fun buildRetrofit() {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(currentBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        apiService = retrofit?.create(ZenithApiService::class.java)
    }

    fun getService(): ZenithApiService {
        if (apiService == null) {
            buildRetrofit()
        }
        return apiService!!
    }

    /**
     * Helper to extract the exact error message returned by FastAPI
     * (e.g. {"detail": "Already registered..."} or {"detail": "Event is full..."})
     */
    fun parseErrorMessage(response: Response<*>): String {
        try {
            val errorBody = response.errorBody()?.string() ?: return "HTTP ${response.code()}: ${response.message()}"
            val parsed = gson.fromJson(errorBody, ApiErrorResponse::class.java)
            if (!parsed?.detail.isNullOrBlank()) {
                return parsed.detail!!
            }
            return errorBody
        } catch (e: Exception) {
            return "HTTP ${response.code()}: ${response.message()}"
        }
    }
}
