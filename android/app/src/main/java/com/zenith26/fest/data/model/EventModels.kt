package com.zenith26.fest.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data model for event items returned by GET /api/events.
 */
data class EventOut(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("category") val category: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("event_date") val eventDate: String,
    @SerializedName("event_time") val eventTime: String,
    @SerializedName("venue") val venue: String,
    @SerializedName("capacity") val capacity: Int,
    @SerializedName("fee") val fee: Int = 0,
    @SerializedName("icon") val icon: String? = "⚡",
    @SerializedName("available_seats") val availableSeats: Int
) {
    val isFull: Boolean
        get() = availableSeats <= 0

    val feeDisplay: String
        get() = if (fee <= 0) "Free" else "₹$fee"

    val registeredCount: Int
        get() = maxOf(0, capacity - availableSeats)

    val utilizationPct: Double
        get() = if (capacity > 0) (registeredCount.toDouble() / capacity.toDouble()) * 100.0 else 0.0

    val isHighDemand: Boolean
        get() = utilizationPct >= 90.0
}

/**
 * Data model for event detail returned by GET /api/events/{event_id}.
 */
typealias EventDetail = EventOut

/**
 * Request payload for POST /api/events/{event_id}/register.
 */
data class RegistrationIn(
    @SerializedName("student_name") val studentName: String,
    @SerializedName("roll_number") val rollNumber: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String? = null
)

/**
 * Confirmation payload returned by POST /api/events/{event_id}/register.
 */
data class RegistrationOut(
    @SerializedName("id") val id: Int,
    @SerializedName("event_id") val eventId: Int,
    @SerializedName("student_name") val studentName: String,
    @SerializedName("registered_at") val registeredAt: String
)

/**
 * Single registration entry returned by GET /api/registrations.
 */
data class RegistrationItemOut(
    @SerializedName("id") val id: Int,
    @SerializedName("event_id") val eventId: Int,
    @SerializedName("event_title") val eventTitle: String,
    @SerializedName("student_name") val studentName: String,
    @SerializedName("roll_number") val rollNumber: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("registered_at") val registeredAt: String
)

/**
 * Stats summary returned by GET /api/stats.
 */
data class StatsOut(
    @SerializedName("total_events") val totalEvents: Int,
    @SerializedName("total_registrations") val totalRegistrations: Int,
    @SerializedName("total_capacity") val totalCapacity: Int,
    @SerializedName("overall_utilization_pct") val overallUtilizationPct: Double,
    @SerializedName("upcoming_events_count") val upcomingEventsCount: Int,
    @SerializedName("events_by_category") val eventsByCategory: Map<String, Int> = emptyMap()
)

/**
 * Request payload for POST /api/events (Create Event).
 */
data class EventCreateIn(
    @SerializedName("title") val title: String,
    @SerializedName("category") val category: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("event_date") val eventDate: String,
    @SerializedName("event_time") val eventTime: String,
    @SerializedName("venue") val venue: String,
    @SerializedName("capacity") val capacity: Int,
    @SerializedName("fee") val fee: Int = 0,
    @SerializedName("icon") val icon: String? = "⚡"
)

/**
 * Standard error response structure from FastAPI HTTPException.
 */
data class ApiErrorResponse(
    @SerializedName("detail") val detail: String? = null
)
