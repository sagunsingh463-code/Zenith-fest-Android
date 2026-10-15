// ==============================================================================
// Zenith '26 College Fest Management System — Student Portal JavaScript
// ==============================================================================

// State management
let currentCategory = "All";
let currentSearch = "";
let searchDebounceTimer = null;
let activeEvent = null;

// DOM Elements
const eventsGrid = document.getElementById("events-grid");
const loadingContainer = document.getElementById("loading-container");
const errorContainer = document.getElementById("error-container");
const errorMessage = document.getElementById("error-message");
const retryBtn = document.getElementById("retry-btn");
const emptyContainer = document.getElementById("empty-container");
const resetFiltersBtn = document.getElementById("reset-filters-btn");
const searchInput = document.getElementById("search-input");
const clearSearchBtn = document.getElementById("clear-search-btn");
const eventsCountBadge = document.getElementById("events-count-badge");
const categoryButtons = document.querySelectorAll(".category-filter-btn");

// Modal Elements
const detailModal = document.getElementById("detail-modal");
const closeModalBtn = document.getElementById("close-modal-btn");
const cancelFormBtn = document.getElementById("cancel-form-btn");
const modalIcon = document.getElementById("modal-icon");
const modalCategory = document.getElementById("modal-category");
const modalFee = document.getElementById("modal-fee");
const modalTitle = document.getElementById("modal-title");
const modalDatetime = document.getElementById("modal-datetime");
const modalVenue = document.getElementById("modal-venue");
const modalSeats = document.getElementById("modal-seats");
const modalDescription = document.getElementById("modal-description");
const modalFormBadge = document.getElementById("modal-form-badge");

// Form Elements
const registrationForm = document.getElementById("registration-form");
const formEventId = document.getElementById("form-event-id");
const formStudentName = document.getElementById("form-student-name");
const formRollNumber = document.getElementById("form-roll-number");
const formEmail = document.getElementById("form-email");
const formPhone = document.getElementById("form-phone");
const formAlert = document.getElementById("form-alert");
const submitRegisterBtn = document.getElementById("submit-register-btn");
const submitBtnSpinner = document.getElementById("submit-btn-spinner");
const submitBtnText = document.getElementById("submit-btn-text");

// Field error labels
const errorStudentName = document.getElementById("error-student-name");
const errorRollNumber = document.getElementById("error-roll-number");
const errorEmail = document.getElementById("error-email");


// ==============================================================================
// Category Badge Color Helper
// ==============================================================================
function getCategoryBadgeClasses(category) {
  const cat = (category || "").toLowerCase();
  switch (cat) {
    case "technical":
      return "bg-cyan-500/20 text-cyan-300 border-cyan-500/30";
    case "cultural":
      return "bg-pink-500/20 text-pink-300 border-pink-500/30";
    case "sports":
      return "bg-emerald-500/20 text-emerald-300 border-emerald-500/30";
    case "gaming":
      return "bg-purple-500/20 text-purple-300 border-purple-500/30";
    case "workshop":
      return "bg-amber-500/20 text-amber-300 border-amber-500/30";
    default:
      return "bg-slate-500/20 text-slate-300 border-slate-500/30";
  }
}


// ==============================================================================
// API Fetching: GET /api/events (with category & search query params)
// Dual-route API fetch helper: tries /api/... then falls back to /...
async function apiFetch(endpoint, options) {
  const cleanEp = endpoint.replace(/^\/+/, '').replace(/^api\//, '');
  try {
    const res1 = await fetch(`/api/${cleanEp}`, options);
    if (res1.ok) return res1;
  } catch (_) {}
  return await fetch(`/${cleanEp}`, options);
}

// ==============================================================================
async function fetchEvents() {
  // Show loading state
  loadingContainer.classList.remove("hidden");
  eventsGrid.classList.add("hidden");
  emptyContainer.classList.add("hidden");
  errorContainer.classList.add("hidden");
  eventsCountBadge.textContent = "Fetching events...";

  const params = new URLSearchParams();
  if (currentCategory && currentCategory !== "All") {
    params.append("category", currentCategory);
  }
  if (currentSearch && currentSearch.trim() !== "") {
    params.append("search", currentSearch.trim());
  }

  const qStr = params.toString() ? "?" + params.toString() : "";

  try {
    const response = await apiFetch(`events${qStr}`);
    if (!response.ok) {
      throw new Error(`Server returned status HTTP ${response.status}`);
    }

    const events = await response.json();
    loadingContainer.classList.add("hidden");

    if (!Array.isArray(events) || events.length === 0) {
      emptyContainer.classList.remove("hidden");
      eventsCountBadge.textContent = "0 events found";
      return;
    }

    renderEventsGrid(events);
    eventsCountBadge.textContent = `Showing ${events.length} event${events.length === 1 ? "" : "s"}`;
    eventsGrid.classList.remove("hidden");
  } catch (err) {
    console.error("Error fetching events:", err);
    loadingContainer.classList.add("hidden");
    errorMessage.textContent = `Could not reach the server (${err.message}). Please ensure FastAPI is running.`;
    errorContainer.classList.remove("hidden");
    eventsCountBadge.textContent = "Failed to load";
  }
}


// ==============================================================================
// Render Event Cards in #events-grid
// ==============================================================================
function renderEventsGrid(events) {
  eventsGrid.innerHTML = "";

  events.forEach((event, index) => {
    const isFull = event.available_seats === 0;
    const feeDisplay = event.fee > 0 ? `₹${event.fee}` : "Free";
    const badgeColors = getCategoryBadgeClasses(event.category);
    const delay = Math.min(index * 40, 400); // Staggered animation feel

    const card = document.createElement("div");
    card.className = "group relative bg-slate-900/90 hover:bg-slate-800/90 border border-slate-800 hover:border-purple-500/40 rounded-2xl p-5 shadow-lg hover:shadow-purple-900/20 transition-all duration-300 flex flex-col justify-between animate-fade-in";
    card.style.animationDelay = `${delay}ms`;

    card.innerHTML = `
      <div>
        <!-- Top Row: Icon + Badges -->
        <div class="flex items-start justify-between gap-3 mb-3">
          <div class="w-12 h-12 rounded-xl bg-slate-800 border border-slate-700/80 flex items-center justify-center text-2xl shadow-inner group-hover:scale-105 transition-transform">
            ${event.icon || "⚡"}
          </div>
          <div class="flex flex-wrap items-center justify-end gap-1.5">
            <span class="text-[11px] font-bold px-2.5 py-0.5 rounded-full border ${badgeColors}">
              ${event.category}
            </span>
            ${
              isFull
                ? `<span class="text-[11px] font-black px-2.5 py-0.5 rounded-full bg-rose-500/20 text-rose-300 border border-rose-500/40 animate-pulse">FULL</span>`
                : `<span class="text-[11px] font-bold px-2 py-0.5 rounded-full bg-emerald-500/20 text-emerald-300 border border-emerald-500/30">${feeDisplay}</span>`
            }
          </div>
        </div>

        <!-- Title -->
        <h3 class="text-lg font-bold text-white group-hover:text-purple-300 transition-colors line-clamp-1">
          ${escapeHtml(event.title)}
        </h3>

        <!-- Description snippet -->
        <p class="mt-1 text-xs text-slate-400 line-clamp-2 leading-relaxed">
          ${escapeHtml(event.description || "No description provided.")}
        </p>

        <!-- Event Details Metadata -->
        <div class="mt-4 pt-3 border-t border-slate-800/80 space-y-1.5 text-xs text-slate-300">
          <div class="flex items-center space-x-2">
            <span class="text-slate-500">📅</span>
            <span>${escapeHtml(event.event_date)} • ${escapeHtml(event.event_time)}</span>
          </div>
          <div class="flex items-center space-x-2">
            <span class="text-slate-500">📍</span>
            <span class="line-clamp-1">${escapeHtml(event.venue)}</span>
          </div>
        </div>
      </div>

      <!-- Footer: Seats & Action Button -->
      <div class="mt-5 pt-3 border-t border-slate-800/80 flex items-center justify-between">
        <div class="text-xs">
          <span class="text-slate-400 block text-[10px] uppercase tracking-wider font-semibold">Available Seats</span>
          ${
            isFull
              ? `<span class="font-bold text-rose-400">0 / ${event.capacity} seats</span>`
              : `<span class="font-bold text-slate-200"><span class="text-purple-400">${event.available_seats}</span> / ${event.capacity} seats</span>`
          }
        </div>

        <button
          type="button"
          data-event-id="${event.id}"
          class="view-details-btn inline-flex items-center space-x-1 text-xs font-semibold px-3.5 py-2 rounded-xl transition active:scale-95 ${
            isFull
              ? "bg-slate-800 text-slate-400 hover:text-white hover:bg-slate-700 border border-slate-700"
              : "bg-purple-600/90 hover:bg-purple-600 text-white shadow-md shadow-purple-600/20 border border-purple-500/50"
          }"
        >
          <span>${isFull ? "View Details" : "Register Now"}</span>
          <span>→</span>
        </button>
      </div>
    `;

    eventsGrid.appendChild(card);
  });

  // Attach click listeners to "View Details" buttons
  document.querySelectorAll(".view-details-btn").forEach((btn) => {
    btn.addEventListener("click", () => {
      const eventId = btn.getAttribute("data-event-id");
      openEventDetail(eventId);
    });
  });
}


// ==============================================================================
// Event Detail & Registration Modal (Step 7)
// ==============================================================================
async function openEventDetail(eventId) {
  clearFormAlert();
  resetFormErrors();
  registrationForm.reset();

  try {
    const res = await apiFetch(`events/${eventId}`);
    if (!res.ok) {
      throw new Error(`Failed to load event details (${res.status})`);
    }
    const event = await res.json();
    activeEvent = event;

    // Populate modal fields
    formEventId.value = event.id;
    modalIcon.textContent = event.icon || "⚡";
    modalTitle.textContent = event.title;
    modalCategory.textContent = event.category;
    modalCategory.className = `text-[11px] font-bold uppercase tracking-wider px-2.5 py-0.5 rounded-full border ${getCategoryBadgeClasses(event.category)}`;
    
    modalFee.textContent = event.fee > 0 ? `Fee: ₹${event.fee}` : "Free Entry";
    modalDatetime.textContent = `${event.event_date} at ${event.event_time}`;
    modalVenue.textContent = event.venue;
    
    if (event.available_seats === 0) {
      modalSeats.innerHTML = `<span class="text-rose-400 font-bold">FULL (0 / ${event.capacity} left)</span>`;
      submitRegisterBtn.disabled = true;
      submitBtnText.textContent = "Event Full";
      modalFormBadge.textContent = "Registrations Closed";
      modalFormBadge.className = "text-[11px] font-bold text-rose-400";
    } else {
      modalSeats.innerHTML = `<span class="text-emerald-400 font-bold">${event.available_seats}</span> / ${event.capacity} seats available`;
      submitRegisterBtn.disabled = false;
      submitBtnText.textContent = "Confirm Registration";
      modalFormBadge.textContent = "Instant Confirmation";
      modalFormBadge.className = "text-[11px] font-semibold text-purple-400";
    }

    modalDescription.textContent = event.description || "No full description available for this event.";

    // Open modal
    detailModal.classList.remove("hidden");
    document.body.classList.add("overflow-hidden");
  } catch (err) {
    alert("Could not load event detail: " + err.message);
  }
}

function closeDetailModal() {
  detailModal.classList.add("hidden");
  document.body.classList.remove("overflow-hidden");
  activeEvent = null;
  registrationForm.reset();
  clearFormAlert();
  resetFormErrors();
}


// ==============================================================================
// Client-Side Form Validation & Submission (Step 7)
// ==============================================================================
function resetFormErrors() {
  errorStudentName.classList.add("hidden");
  errorStudentName.textContent = "";
  formStudentName.classList.remove("border-rose-500", "focus:ring-rose-500");

  errorRollNumber.classList.add("hidden");
  errorRollNumber.textContent = "";
  formRollNumber.classList.remove("border-rose-500", "focus:ring-rose-500");

  errorEmail.classList.add("hidden");
  errorEmail.textContent = "";
  formEmail.classList.remove("border-rose-500", "focus:ring-rose-500");
}

function showFieldError(inputElem, errorElem, message) {
  errorElem.textContent = message;
  errorElem.classList.remove("hidden");
  inputElem.classList.add("border-rose-500", "focus:ring-rose-500");
}

function showFormAlert(message, isSuccess = false) {
  formAlert.classList.remove("hidden", "bg-rose-950/60", "border-rose-800", "text-rose-200", "bg-emerald-950/60", "border-emerald-800", "text-emerald-200");
  if (isSuccess) {
    formAlert.classList.add("bg-emerald-950/60", "border", "border-emerald-700", "text-emerald-200");
    formAlert.innerHTML = `✅ <strong>Success!</strong> ${message}`;
  } else {
    formAlert.classList.add("bg-rose-950/60", "border", "border-rose-800", "text-rose-200");
    formAlert.innerHTML = `⚠️ <strong>Registration Error:</strong> ${message}`;
  }
}

function clearFormAlert() {
  formAlert.classList.add("hidden");
  formAlert.innerHTML = "";
}

async function handleRegistrationSubmit(e) {
  e.preventDefault();
  resetFormErrors();
  clearFormAlert();

  const eventId = formEventId.value;
  const name = formStudentName.value.trim();
  const roll = formRollNumber.value.trim();
  const email = formEmail.value.trim();
  const phone = formPhone.value.trim();

  // Basic client-side validation
  let hasErrors = false;

  if (!name) {
    showFieldError(formStudentName, errorStudentName, "Please enter your full name");
    hasErrors = true;
  }

  if (!roll) {
    showFieldError(formRollNumber, errorRollNumber, "Please enter your college roll number");
    hasErrors = true;
  }

  if (!email) {
    showFieldError(formEmail, errorEmail, "Please enter your email address");
    hasErrors = true;
  } else if (!email.includes("@") || email.startsWith("@") || email.endsWith("@")) {
    showFieldError(formEmail, errorEmail, "Please enter a valid email address containing '@'");
    hasErrors = true;
  }

  if (hasErrors) return;

  // Set submitting state
  submitRegisterBtn.disabled = true;
  submitBtnSpinner.classList.remove("hidden");
  submitBtnText.textContent = "Registering...";

  try {
    const response = await apiFetch(`events/${eventId}/register`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        student_name: name,
        roll_number: roll,
        email: email,
        phone: phone || null,
      }),
    });

    const data = await response.json();

    if (response.status === 201) {
      // 201 Success confirmation
      showFormAlert(`You are confirmed for <strong>${escapeHtml(activeEvent.title)}</strong>! Registration ID: #${data.id}. See you there!`, true);
      
      // Update seat count locally & re-fetch
      if (activeEvent.available_seats > 0) {
        activeEvent.available_seats -= 1;
        modalSeats.innerHTML = `<span class="text-emerald-400 font-bold">${activeEvent.available_seats}</span> / ${activeEvent.capacity} seats available`;
      }

      // Re-fetch events list to sync grid seats count
      fetchEvents();

      // Clear form inputs
      registrationForm.reset();

      // Auto close after brief moment
      setTimeout(() => {
        closeDetailModal();
      }, 2400);

    } else {
      // 400 or 409 or other server-side validation error
      const serverErrorMsg = data.detail || "Unable to complete registration. Please check your details.";
      showFormAlert(serverErrorMsg, false);
      submitRegisterBtn.disabled = false;
      submitBtnText.textContent = "Try Again";
    }
  } catch (err) {
    console.error("Submission failed:", err);
    showFormAlert("Network error occurred while connecting to the server. Please check your connection.", false);
    submitRegisterBtn.disabled = false;
    submitBtnText.textContent = "Try Again";
  } finally {
    submitBtnSpinner.classList.add("hidden");
  }
}


// ==============================================================================
// Search & Filter Interactions
// ==============================================================================

// Category filter button clicks
categoryButtons.forEach((btn) => {
  btn.addEventListener("click", () => {
    categoryButtons.forEach((b) => {
      b.classList.remove("bg-purple-600", "text-white", "shadow-lg", "shadow-purple-600/30", "border-purple-500");
      b.classList.add("bg-slate-900", "text-slate-300", "border-slate-800");
    });

    btn.classList.add("bg-purple-600", "text-white", "shadow-lg", "shadow-purple-600/30", "border-purple-500");
    btn.classList.remove("bg-slate-900", "text-slate-300", "border-slate-800");

    currentCategory = btn.getAttribute("data-category");
    fetchEvents();
  });
});

// Search input debounced handler
searchInput.addEventListener("input", (e) => {
  const val = e.target.value;
  clearSearchBtn.classList.toggle("hidden", val.length === 0);

  clearTimeout(searchDebounceTimer);
  searchDebounceTimer = setTimeout(() => {
    currentSearch = val;
    fetchEvents();
  }, 250);
});

// Clear search button
clearSearchBtn.addEventListener("click", () => {
  searchInput.value = "";
  currentSearch = "";
  clearSearchBtn.classList.add("hidden");
  fetchEvents();
});

// Reset all filters button
resetFiltersBtn.addEventListener("click", () => {
  searchInput.value = "";
  currentSearch = "";
  clearSearchBtn.classList.add("hidden");
  currentCategory = "All";
  categoryButtons.forEach((b) => {
    if (b.getAttribute("data-category") === "All") {
      b.classList.add("bg-purple-600", "text-white", "shadow-lg", "shadow-purple-600/30", "border-purple-500");
      b.classList.remove("bg-slate-900", "text-slate-300", "border-slate-800");
    } else {
      b.classList.remove("bg-purple-600", "text-white", "shadow-lg", "shadow-purple-600/30", "border-purple-500");
      b.classList.add("bg-slate-900", "text-slate-300", "border-slate-800");
    }
  });
  fetchEvents();
});

// Retry button on error
retryBtn.addEventListener("click", fetchEvents);

// Modal listeners
closeModalBtn.addEventListener("click", closeDetailModal);
cancelFormBtn.addEventListener("click", closeDetailModal);
detailModal.addEventListener("click", (e) => {
  if (e.target === detailModal) {
    closeDetailModal();
  }
});
document.addEventListener("keydown", (e) => {
  if (e.key === "Escape" && !detailModal.classList.contains("hidden")) {
    closeDetailModal();
  }
});

// Registration form submission
registrationForm.addEventListener("submit", handleRegistrationSubmit);


// ==============================================================================
// Utility Functions
// ==============================================================================
function escapeHtml(str) {
  if (!str) return "";
  return String(str)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
}


// ==============================================================================
// Initial Page Load
// ==============================================================================
document.addEventListener("DOMContentLoaded", () => {
  fetchEvents();
});
