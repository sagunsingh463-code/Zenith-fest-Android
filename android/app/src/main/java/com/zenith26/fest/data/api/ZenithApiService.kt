package com.zenith26.fest.data.api

import com.zenith26.fest.data.model.EventCreateIn
import com.zenith26.fest.data.model.EventDetail
import com.zenith26.fest.data.model.EventOut
import com.zenith26.fest.data.model.RegistrationIn
import com.zenith26.fest.data.model.RegistrationItemOut
import com.zenith26.fest.data.model.RegistrationOut
import com.zenith26.fest.data.model.StatsOut
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface representing the exact Zenith '26 REST API contract.
 */
interface ZenithApiService {

    @GET("api/events")
    suspend fun getEvents(
        @Query("category") category: String? = null,
        @Query("search") search: String? = null
    ): Response<List<EventOut>>

    @GET("api/events/{event_id}")
    suspend fun getEventDetail(
        @Path("event_id") eventId: Int
    ): Response<EventDetail>

    @POST("api/events/{event_id}/register")
    suspend fun registerStudent(
        @Path("event_id") eventId: Int,
        @Body payload: RegistrationIn
    ): Response<RegistrationOut>

    @GET("api/registrations")
    suspend fun getRegistrations(
        @Query("event_id") eventId: Int? = null
    ): Response<List<RegistrationItemOut>>

    @GET("api/stats")
    suspend fun getStats(): Response<StatsOut>

    @POST("api/events")
    suspend fun createEvent(
        @Body payload: EventCreateIn
    ): Response<EventOut>

    @DELETE("api/registrations/{registration_id}")
    suspend fun cancelRegistration(
        @Path("registration_id") registrationId: Int
    ): Response<Map<String, Any>>
}
