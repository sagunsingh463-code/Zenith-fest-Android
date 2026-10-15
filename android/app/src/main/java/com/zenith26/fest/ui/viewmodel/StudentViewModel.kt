package com.zenith26.fest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zenith26.fest.data.model.EventOut
import com.zenith26.fest.data.model.RegistrationIn
import com.zenith26.fest.data.repository.ZenithRepository
import com.zenith26.fest.ui.components.RegistrationFormStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel managing the Student Event Portal state and interactions.
 */
class StudentViewModel(
    private val repository: ZenithRepository = ZenithRepository()
) : ViewModel() {

    private val _events = MutableStateFlow<List<EventOut>>(emptyList())
    val events: StateFlow<List<EventOut>> = _events.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedEvent = MutableStateFlow<EventOut?>(null)
    val selectedEvent: StateFlow<EventOut?> = _selectedEvent.asStateFlow()

    private val _registrationStatus = MutableStateFlow<RegistrationFormStatus>(RegistrationFormStatus.Idle)
    val registrationStatus: StateFlow<RegistrationFormStatus> = _registrationStatus.asStateFlow()

    private var searchDebounceJob: Job? = null

    init {
        loadEvents()
    }

    fun loadEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = repository.getEvents(
                category = _selectedCategory.value,
                search = _searchQuery.value
            )

            result.onSuccess { list ->
                _events.value = list
                _isLoading.value = false
            }.onFailure { err ->
                _isLoading.value = false
                _errorMessage.value = err.message ?: "Failed to connect to backend server"
            }
        }
    }

    fun selectCategory(category: String) {
        if (_selectedCategory.value == category) return
        _selectedCategory.value = category
        loadEvents()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        searchDebounceJob?.cancel()
        searchDebounceJob = viewModelScope.launch {
            delay(300) // Debounce 300ms
            loadEvents()
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        loadEvents()
    }

    fun resetFilters() {
        _selectedCategory.value = "All"
        _searchQuery.value = ""
        loadEvents()
    }

    fun openEventDetail(event: EventOut) {
        _registrationStatus.value = RegistrationFormStatus.Idle
        // Fetch fresh detail from GET /api/events/{event_id}
        viewModelScope.launch {
            val freshResult = repository.getEventDetail(event.id)
            if (freshResult.isSuccess) {
                _selectedEvent.value = freshResult.getOrNull()
            } else {
                _selectedEvent.value = event
            }
        }
    }

    fun closeEventDetail() {
        _selectedEvent.value = null
        _registrationStatus.value = RegistrationFormStatus.Idle
    }

    fun submitRegistration(eventId: Int, payload: RegistrationIn) {
        viewModelScope.launch {
            _registrationStatus.value = RegistrationFormStatus.Submitting

            val result = repository.registerStudent(eventId, payload)

            result.onSuccess { regOut ->
                val currentEv = _selectedEvent.value
                val eventTitle = currentEv?.title ?: "this event"
                val successMsg = "You are confirmed for $eventTitle! Registration ID: #${regOut.id}. See you there!"
                _registrationStatus.value = RegistrationFormStatus.Success(successMsg)

                // Decrement seats locally immediately
                if (currentEv != null && currentEv.availableSeats > 0) {
                    val updated = currentEv.copy(availableSeats = currentEv.availableSeats - 1)
                    _selectedEvent.value = updated
                }

                // Re-fetch events list to keep grid in sync
                loadEvents()

                // Auto close modal after brief confirmation period
                delay(2400)
                closeEventDetail()

            }.onFailure { err ->
                // Preserve exact server-side validation error
                _registrationStatus.value = RegistrationFormStatus.Error(
                    err.message ?: "Registration could not be completed"
                )
            }
        }
    }
}
