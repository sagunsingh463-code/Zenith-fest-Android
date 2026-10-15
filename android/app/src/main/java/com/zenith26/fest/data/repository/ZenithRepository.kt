package com.zenith26.fest.data.repository

import com.zenith26.fest.data.api.ApiClient
import com.zenith26.fest.data.model.EventCreateIn
import com.zenith26.fest.data.model.EventDetail
import com.zenith26.fest.data.model.EventOut
import com.zenith26.fest.data.model.RegistrationIn
import com.zenith26.fest.data.model.RegistrationItemOut
import com.zenith26.fest.data.model.RegistrationOut
import com.zenith26.fest.data.model.StatsOut
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Repository layer communicating with FastAPI endpoints through ZenithApiService.
 */
class ZenithRepository {

    private val api get() = ApiClient.getService()

    suspend fun getEvents(category: String?, search: String?): Result<List<EventOut>> = withContext(Dispatchers.IO) {
        try {
            val catParam = if (category.isNullOrBlank() || category.equals("All", ignoreCase = true)) null else category
            val searchParam = if (search.isNullOrBlank()) null else search
            val response = api.getEvents(catParam, searchParam)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(ApiClient.parseErrorMessage(response)))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Could not connect to server. Ensure FastAPI is running at ${ApiClient.getBaseUrl()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEventDetail(eventId: Int): Result<EventDetail> = withContext(Dispatchers.IO) {
        try {
            val response = api.getEventDetail(eventId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(ApiClient.parseErrorMessage(response)))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Could not connect to server. Ensure FastAPI is running."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerStudent(eventId: Int, payload: RegistrationIn): Result<RegistrationOut> = withContext(Dispatchers.IO) {
        try {
            val response = api.registerStudent(eventId, payload)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = ApiClient.parseErrorMessage(response)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Network error: Could not connect to server."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStats(): Result<StatsOut> = withContext(Dispatchers.IO) {
        try {
            val response = api.getStats()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(ApiClient.parseErrorMessage(response)))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Could not connect to server. Ensure FastAPI is running."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRegistrations(eventId: Int?): Result<List<RegistrationItemOut>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getRegistrations(eventId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(ApiClient.parseErrorMessage(response)))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Could not connect to server. Ensure FastAPI is running."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createEvent(payload: EventCreateIn): Result<EventOut> = withContext(Dispatchers.IO) {
        try {
            val response = api.createEvent(payload)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(ApiClient.parseErrorMessage(response)))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Could not reach backend."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelRegistration(regId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.cancelRegistration(regId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(ApiClient.parseErrorMessage(response)))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Network error: Could not reach server."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
