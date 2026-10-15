package com.zenith26.fest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zenith26.fest.data.model.EventCreateIn
import com.zenith26.fest.data.model.EventOut
import com.zenith26.fest.data.model.RegistrationItemOut
import com.zenith26.fest.data.model.StatsOut
import com.zenith26.fest.data.repository.ZenithRepository
import com.zenith26.fest.ui.components.CreateEventStatus
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel managing the Admin Dashboard state, live stats, utilization table, and registrations.
 */
class AdminViewModel(
    private val repository: ZenithRepository = ZenithRepository()
) : ViewModel() {

    private val _stats = MutableStateFlow<StatsOut?>(null)
    val stats: StateFlow<StatsOut?> = _stats.asStateFlow()

    private val _events = MutableStateFlow<List<EventOut>>(emptyList())
    val events: StateFlow<List<EventOut>> = _events.asStateFlow()

    private val _registrations = MutableStateFlow<List<RegistrationItemOut>>(emptyList())
    val registrations: StateFlow<List<RegistrationItemOut>> = _registrations.asStateFlow()

    private val _selectedEventFilter = MutableStateFlow<Int?>(null)
    val selectedEventFilter: StateFlow<Int?> = _selectedEventFilter.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _createEventStatus = MutableStateFlow<CreateEventStatus>(CreateEventStatus.Idle)
    val createEventStatus: StateFlow<CreateEventStatus> = _createEventStatus.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val statsDeferred = async { repository.getStats() }
            val eventsDeferred = async { repository.getEvents(category = null, search = null) }
            val regsDeferred = async { repository.getRegistrations(eventId = _selectedEventFilter.value) }

            val statsRes = statsDeferred.await()
            val eventsRes = eventsDeferred.await()
            val regsRes = regsDeferred.await()

            if (statsRes.isSuccess) {
                _stats.value = statsRes.getOrNull()
            }
            if (eventsRes.isSuccess) {
                _events.value = eventsRes.getOrNull() ?: emptyList()
            }
            if (regsRes.isSuccess) {
                _registrations.value = regsRes.getOrNull() ?: emptyList()
            }

            if (statsRes.isFailure && eventsRes.isFailure) {
                _errorMessage.value = statsRes.exceptionOrNull()?.message ?: "Failed to load dashboard data"
            }

            _isLoading.value = false
        }
    }

    fun filterRegistrationsByEvent(eventId: Int?) {
        _selectedEventFilter.value = eventId
        viewModelScope.launch {
            val res = repository.getRegistrations(eventId)
            res.onSuccess {
                _registrations.value = it
            }
        }
    }

    fun createEvent(payload: EventCreateIn, onComplete: () -> Unit) {
        viewModelScope.launch {
            _createEventStatus.value = CreateEventStatus.Submitting

            val res = repository.createEvent(payload)
            res.onSuccess { created ->
                _createEventStatus.value = CreateEventStatus.Success("Event \"${created.title}\" successfully created! (ID: #${created.id})")
                loadDashboard()
                delay(1600)
                _createEventStatus.value = CreateEventStatus.Idle
                onComplete()
            }.onFailure { err ->
                _createEventStatus.value = CreateEventStatus.Error(err.message ?: "Failed to create event")
            }
        }
    }

    fun resetCreateEventStatus() {
        _createEventStatus.value = CreateEventStatus.Idle
    }

    fun cancelRegistration(regId: Int) {
        viewModelScope.launch {
            val res = repository.cancelRegistration(regId)
            res.onSuccess {
                // Refresh dashboard immediately to restore available seat
                loadDashboard()
            }
        }
    }
}
