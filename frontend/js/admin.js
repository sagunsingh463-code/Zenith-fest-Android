// ==============================================================================
// Zenith '26 College Fest Management System — Admin Dashboard JavaScript
// ==============================================================================

// Global dashboard state
let cachedEvents = [];
let cachedRegistrations = [];
let currentEventFilter = "";

// DOM Elements: Metrics
const statTotalEvents = document.getElementById("stat-total-events");
const statTotalRegistrations = document.getElementById("stat-total-registrations");
const statUtilizationPct = document.getElementById("stat-utilization-pct");
const statTotalCapacity = document.getElementById("stat-total-capacity");
const statUtilizationBar = document.getElementById("stat-utilization-bar");
const statUpcomingEvents = document.getElementById("stat-upcoming-events");
const categoryBreakdownList = document.getElementById("category-breakdown-list");
const statsTimestamp = document.getElementById("stats-timestamp");

// DOM Elements: Tabs & Badges
const tabBtnEvents = document.getElementById("tab-btn-events");
const tabBtnRegistrations = document.getElementById("tab-btn-registrations");
const tabContentEvents = document.getElementById("tab-content-events");
const tabContentRegistrations = document.getElementById("tab-content-registrations");
const tableEventsCountBadge = document.getElementById("table-events-count-badge");
const tableRegsCountBadge = document.getElementById("table-regs-count-badge");
const filteredRegCount = document.getElementById("filtered-reg-count");

// DOM Elements: Tables
const eventsTableBody = document.getElementById("events-table-body");
const registrationsTableBody = document.getElementById("registrations-table-body");
const regEventFilter = document.getElementById("reg-event-filter");
const refreshDashboardBtn = document.getElementById("refresh-dashboard-btn");

// DOM Elements: Create Event Modal
const openCreateEventBtn = document.getElementById("open-create-event-btn");
const closeCreateEventBtn = document.getElementById("close-create-event-btn");
const cancelCreateEventBtn = document.getElementById("cancel-create-event-btn");
const createEventModal = document.getElementById("create-event-modal");
const createEventForm = document.getElementById("create-event-form");
const createEventAlert = document.getElementById("create-event-alert");


// ==============================================================================
// Category Badge Class Helper
// ==============================================================================
function getCategoryBadge(category) {
  const cat = (category || "").toLowerCase();
  switch (cat) {
    case "technical":
      return "bg-cyan-500/10 text-cyan-300 border-cyan-500/30";
    case "cultural":
      return "bg-pink-500/10 text-pink-300 border-pink-500/30";
    case "sports":
      return "bg-emerald-500/10 text-emerald-300 border-emerald-500/30";
    case "gaming":
      return "bg-purple-500/10 text-purple-300 border-purple-500/30";
    case "workshop":
      return "bg-amber-500/10 text-amber-300 border-amber-500/30";
    default:
      return "bg-slate-500/10 text-slate-300 border-slate-500/30";
  }
}


// ==============================================================================
// 1. Fetch Live Stats (GET /api/stats)
// ==============================================================================
async function loadStats() {
  try {
    const res = await fetch("/api/stats");
    if (!res.ok) throw new Error(`Stats fetch failed (${res.status})`);
    const data = await res.json();

    statTotalEvents.textContent = data.total_events;
    statTotalRegistrations.textContent = data.total_registrations;
    statUtilizationPct.textContent = `${data.overall_utilization_pct.toFixed(1)}%`;
    statTotalCapacity.textContent = `of ${data.total_capacity} seats`;
    statUpcomingEvents.textContent = data.upcoming_events_count;

    // Progress bar width
    const clampedPct = Math.min(Math.max(data.overall_utilization_pct, 0), 100);
    statUtilizationBar.style.width = `${clampedPct}%`;

    // Render category pills
    categoryBreakdownList.innerHTML = "";
    const categories = Object.keys(data.events_by_category || {});
    if (categories.length === 0) {
      categoryBreakdownList.innerHTML = `<span class="text-xs text-slate-500">No categories found</span>`;
    } else {
      categories.forEach((cat) => {
        const count = data.events_by_category[cat];
        const pill = document.createElement("div");
        pill.className = `inline-flex items-center space-x-1.5 px-3 py-1 rounded-xl text-xs font-semibold border ${getCategoryBadge(cat)}`;
        pill.innerHTML = `<span>${cat}</span><span class="px-1.5 py-0.2 bg-slate-900/80 rounded-md text-[10px] font-bold text-white">${count}</span>`;
        categoryBreakdownList.appendChild(pill);
      });
    }

    statsTimestamp.textContent = `Updated: ${new Date().toLocaleTimeString()}`;
  } catch (err) {
    console.error("Error loading stats:", err);
    statTotalEvents.textContent = "Err";
    statTotalRegistrations.textContent = "Err";
    statUtilizationPct.textContent = "Err";
    statUpcomingEvents.textContent = "Err";
  }
}


// ==============================================================================
// 2. Fetch & Render Events Table (GET /api/events)
// ==============================================================================
async function loadEventsTable() {
  eventsTableBody.innerHTML = `
    <tr>
      <td colspan="7" class="py-8 text-center text-slate-500">
        <div class="inline-block animate-spin rounded-full h-5 w-5 border-2 border-purple-500 border-t-transparent mr-2 align-middle"></div>
        Loading live events and computing utilization...
      </td>
    </tr>
  `;

  try {
    const res = await fetch("/api/events");
    if (!res.ok) throw new Error(`Events fetch failed (${res.status})`);
    const events = await res.json();
    cachedEvents = events;
    tableEventsCountBadge.textContent = events.length;

    // Compute utilization and sort descending
    const processedEvents = events.map((ev) => {
      const registered = ev.capacity - ev.available_seats;
      const utilPct = ev.capacity > 0 ? (registered / ev.capacity) * 100 : 0;
      return {
        ...ev,
        registered,
        utilPct,
      };
    });

    // Sort by utilization percentage descending (most in-demand first)
    processedEvents.sort((a, b) => b.utilPct - a.utilPct);

    if (processedEvents.length === 0) {
      eventsTableBody.innerHTML = `
        <tr>
          <td colspan="7" class="py-8 text-center text-slate-400">
            No events registered in database.
          </td>
        </tr>
      `;
      return;
    }

    eventsTableBody.innerHTML = "";
    processedEvents.forEach((ev) => {
      const isHighDemand = ev.utilPct >= 90;
      const isFull = ev.available_seats === 0;

      const row = document.createElement("tr");
      row.className = `transition-colors duration-150 ${
        isHighDemand ? "bg-rose-950/20 hover:bg-rose-950/30" : "hover:bg-slate-800/40"
      }`;

      // Status pill
      let statusBadge = "";
      if (isFull) {
        statusBadge = `<span class="px-2.5 py-0.5 rounded-full text-[10px] font-black bg-rose-500/20 text-rose-300 border border-rose-500/40">FULL</span>`;
      } else if (isHighDemand) {
        statusBadge = `<span class="px-2 py-0.5 rounded-full text-[10px] font-bold bg-amber-500/20 text-amber-300 border border-amber-500/40">⚠ ALMOST FULL</span>`;
      } else {
        statusBadge = `<span class="px-2 py-0.5 rounded-full text-[10px] font-semibold bg-emerald-500/20 text-emerald-300 border border-emerald-500/30">OPEN</span>`;
      }

      // Bar width
      const barColor = isHighDemand ? "bg-rose-500" : ev.utilPct > 50 ? "bg-amber-500" : "bg-purple-500";

      row.innerHTML = `
        <td class="py-3.5 px-4 font-semibold text-white">
          <div class="flex items-center space-x-2">
            <span class="text-base">${ev.icon || "⚡"}</span>
            <div>
              <span class="block">${escapeHtml(ev.title)}</span>
              <span class="text-[10px] text-slate-400 font-normal">ID: #${ev.id} • Fee: ${ev.fee > 0 ? `₹${ev.fee}` : "Free"}</span>
            </div>
          </div>
        </td>
        <td class="py-3.5 px-4">
          <span class="px-2.5 py-0.5 rounded-full text-[10px] font-semibold border ${getCategoryBadge(ev.category)}">
            ${ev.category}
          </span>
        </td>
        <td class="py-3.5 px-4 text-slate-300">
          <div>${ev.event_date}</div>
          <div class="text-[10px] text-slate-500">${ev.event_time}</div>
        </td>
        <td class="py-3.5 px-4 text-slate-300">
          ${escapeHtml(ev.venue)}
        </td>
        <td class="py-3.5 px-4 font-medium">
          <div class="flex items-center space-x-1.5">
            <span class="${isHighDemand ? "text-rose-400 font-bold" : "text-white font-semibold"}">
              ${ev.registered}
            </span>
            <span class="text-slate-500">/</span>
            <span class="text-slate-400">${ev.capacity}</span>
          </div>
        </td>
        <td class="py-3.5 px-4 text-right">
          <div class="flex flex-col items-end">
            <span class="font-bold text-xs ${isHighDemand ? "text-rose-400" : "text-slate-200"}">
              ${ev.utilPct.toFixed(1)}%
            </span>
            <div class="w-16 bg-slate-800 h-1 rounded-full mt-1 overflow-hidden">
              <div class="${barColor} h-1 rounded-full" style="width: ${Math.min(ev.utilPct, 100)}%"></div>
            </div>
          </div>
        </td>
        <td class="py-3.5 px-4 text-center">
          ${statusBadge}
        </td>
      `;

      eventsTableBody.appendChild(row);
    });

    // Populate dropdown in registrations filter
    updateRegEventDropdown(events);
  } catch (err) {
    console.error("Failed to load events table:", err);
    eventsTableBody.innerHTML = `
      <tr>
        <td colspan="7" class="py-8 text-center text-rose-400">
          Failed to load events (${err.message}). Is the backend active?
        </td>
      </tr>
    `;
  }
}

function updateRegEventDropdown(events) {
  const currentVal = regEventFilter.value;
  regEventFilter.innerHTML = `<option value="">All Events (${events.length})</option>`;
  events.forEach((ev) => {
    const opt = document.createElement("option");
    opt.value = ev.id;
    opt.textContent = `#${ev.id} - ${ev.title} (${ev.category})`;
    if (String(ev.id) === currentVal) {
      opt.selected = true;
    }
    regEventFilter.appendChild(opt);
  });
}


// ==============================================================================
// 3. Fetch & Render Raw Registrations List (GET /api/registrations)
// ==============================================================================
async function loadRegistrationsTable() {
  registrationsTableBody.innerHTML = `
    <tr>
      <td colspan="8" class="py-8 text-center text-slate-500">
        <div class="inline-block animate-spin rounded-full h-5 w-5 border-2 border-purple-500 border-t-transparent mr-2 align-middle"></div>
        Loading registrations...
      </td>
    </tr>
  `;

  let url = "/api/registrations";
  if (currentEventFilter) {
    url += `?event_id=${encodeURIComponent(currentEventFilter)}`;
  }

  try {
    const res = await fetch(url);
    if (!res.ok) throw new Error(`Failed to load registrations (${res.status})`);
    const regs = await res.json();
    cachedRegistrations = regs;
    
    tableRegsCountBadge.textContent = regs.length;
    filteredRegCount.textContent = regs.length;

    if (regs.length === 0) {
      registrationsTableBody.innerHTML = `
        <tr>
          <td colspan="8" class="py-8 text-center text-slate-400">
            No registrations found ${currentEventFilter ? "for this event" : "yet"}.
          </td>
        </tr>
      `;
      return;
    }

    registrationsTableBody.innerHTML = "";
    regs.forEach((r) => {
      const tr = document.createElement("tr");
      tr.className = "hover:bg-slate-800/40 transition-colors duration-150";

      tr.innerHTML = `
        <td class="py-3 px-4 font-mono text-[11px] text-purple-400 font-semibold">
          #${r.id}
        </td>
        <td class="py-3 px-4 font-bold text-white">
          ${escapeHtml(r.student_name)}
        </td>
        <td class="py-3 px-4 font-mono text-xs text-slate-300">
          ${escapeHtml(r.roll_number)}
        </td>
        <td class="py-3 px-4 text-slate-300">
          <a href="mailto:${escapeHtml(r.email)}" class="hover:text-purple-300 underline decoration-slate-700">
            ${escapeHtml(r.email)}
          </a>
        </td>
        <td class="py-3 px-4 text-slate-400 text-xs">
          ${r.phone ? escapeHtml(r.phone) : `<span class="text-slate-600">—</span>`}
        </td>
        <td class="py-3 px-4 text-slate-200 font-medium">
          <span class="line-clamp-1">${escapeHtml(r.event_title)}</span>
        </td>
        <td class="py-3 px-4 text-slate-400 text-[11px] whitespace-nowrap">
          ${r.registered_at}
        </td>
        <td class="py-3 px-4 text-center">
          <button
            type="button"
            data-reg-id="${r.id}"
            data-student-name="${escapeHtml(r.student_name)}"
            data-event-title="${escapeHtml(r.event_title)}"
            class="cancel-reg-btn text-[11px] px-2.5 py-1 rounded bg-rose-950/60 hover:bg-rose-900 text-rose-300 border border-rose-800 transition active:scale-95"
            title="Cancel Registration"
          >
            Cancel
          </button>
        </td>
      `;

      registrationsTableBody.appendChild(tr);
    });

    // Attach listener to cancel buttons (Stretch Goal 2)
    document.querySelectorAll(".cancel-reg-btn").forEach((btn) => {
      btn.addEventListener("click", () => {
        const id = btn.getAttribute("data-reg-id");
        const student = btn.getAttribute("data-student-name");
        const eventTitle = btn.getAttribute("data-event-title");
        handleCancelRegistration(id, student, eventTitle);
      });
    });

  } catch (err) {
    console.error("Error loading registrations:", err);
    registrationsTableBody.innerHTML = `
      <tr>
        <td colspan="8" class="py-8 text-center text-rose-400">
          Failed to load registrations (${err.message}).
        </td>
      </tr>
    `;
  }
}


// ==============================================================================
// 4. Cancel Registration (Stretch Goal 2: DELETE /api/registrations/{id})
// ==============================================================================
async function handleCancelRegistration(id, studentName, eventTitle) {
  const confirmed = confirm(
    `Are you sure you want to cancel the registration for:\nStudent: ${studentName}\nEvent: ${eventTitle}\n\nThis will free up one seat immediately.`
  );
  if (!confirmed) return;

  try {
    const res = await fetch(`/api/registrations/${id}`, {
      method: "DELETE",
    });

    if (res.ok) {
      // Refresh all data
      loadStats();
      loadEventsTable();
      loadRegistrationsTable();
    } else {
      const err = await res.json();
      alert(`Could not cancel registration: ${err.detail || "Server error"}`);
    }
  } catch (err) {
    alert("Network error: " + err.message);
  }
}


// ==============================================================================
// 5. Create Event (Stretch Goal 1: POST /api/events)
// ==============================================================================
async function handleCreateEvent(e) {
  e.preventDefault();
  createEventAlert.classList.add("hidden");

  const title = document.getElementById("new-event-title").value.trim();
  const category = document.getElementById("new-event-category").value;
  const icon = document.getElementById("new-event-icon").value.trim() || "⚡";
  const event_date = document.getElementById("new-event-date").value;
  const event_time = document.getElementById("new-event-time").value.trim();
  const venue = document.getElementById("new-event-venue").value.trim();
  const capacity = parseInt(document.getElementById("new-event-capacity").value, 10);
  const fee = parseInt(document.getElementById("new-event-fee").value, 10) || 0;
  const description = document.getElementById("new-event-description").value.trim();

  if (!title || !category || !venue || !event_date || !event_time) {
    showCreateAlert("Please fill in all required fields.", false);
    return;
  }

  if (isNaN(capacity) || capacity <= 0) {
    showCreateAlert("Capacity must be greater than 0.", false);
    return;
  }

  try {
    const res = await fetch("/api/events", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        title,
        category,
        icon,
        event_date,
        event_time,
        venue,
        capacity,
        fee,
        description,
      }),
    });

    const data = await res.json();

    if (res.status === 201) {
      showCreateAlert(`Event "${data.title}" successfully created! (ID: #${data.id})`, true);
      createEventForm.reset();
      loadStats();
      loadEventsTable();

      setTimeout(() => {
        closeCreateEventModal();
      }, 1600);
    } else {
      showCreateAlert(data.detail || "Failed to create event", false);
    }
  } catch (err) {
    showCreateAlert("Network error: " + err.message, false);
  }
}

function showCreateAlert(msg, isSuccess) {
  createEventAlert.classList.remove("hidden", "bg-emerald-950/60", "border-emerald-800", "text-emerald-200", "bg-rose-950/60", "border-rose-800", "text-rose-200");
  if (isSuccess) {
    createEventAlert.classList.add("bg-emerald-950/60", "border", "border-emerald-800", "text-emerald-200");
    createEventAlert.textContent = "✅ " + msg;
  } else {
    createEventAlert.classList.add("bg-rose-950/60", "border", "border-rose-800", "text-rose-200");
    createEventAlert.textContent = "⚠️ " + msg;
  }
}

function openCreateEventModal() {
  createEventAlert.classList.add("hidden");
  createEventModal.classList.remove("hidden");
}

function closeCreateEventModal() {
  createEventModal.classList.add("hidden");
  createEventForm.reset();
  createEventAlert.classList.add("hidden");
}


// ==============================================================================
// Tab Switcher Logic
// ==============================================================================
tabBtnEvents.addEventListener("click", () => {
  tabBtnEvents.classList.add("border-purple-500", "text-white");
  tabBtnEvents.classList.remove("border-transparent", "text-slate-400");
  tabBtnRegistrations.classList.remove("border-purple-500", "text-white");
  tabBtnRegistrations.classList.add("border-transparent", "text-slate-400");

  tabContentEvents.classList.remove("hidden");
  tabContentRegistrations.classList.add("hidden");
});

tabBtnRegistrations.addEventListener("click", () => {
  tabBtnRegistrations.classList.add("border-purple-500", "text-white");
  tabBtnRegistrations.classList.remove("border-transparent", "text-slate-400");
  tabBtnEvents.classList.remove("border-purple-500", "text-white");
  tabBtnEvents.classList.add("border-transparent", "text-slate-400");

  tabContentRegistrations.classList.remove("hidden");
  tabContentEvents.classList.add("hidden");
  loadRegistrationsTable();
});

// Event filter change
regEventFilter.addEventListener("change", (e) => {
  currentEventFilter = e.target.value;
  loadRegistrationsTable();
});

// Refresh button
refreshDashboardBtn.addEventListener("click", () => {
  loadStats();
  loadEventsTable();
  if (!tabContentRegistrations.classList.contains("hidden")) {
    loadRegistrationsTable();
  }
});

// Create event modal bindings
openCreateEventBtn.addEventListener("click", openCreateEventModal);
closeCreateEventBtn.addEventListener("click", closeCreateEventModal);
cancelCreateEventBtn.addEventListener("click", closeCreateEventModal);
createEventModal.addEventListener("click", (e) => {
  if (e.target === createEventModal) closeCreateEventModal();
});
createEventForm.addEventListener("submit", handleCreateEvent);


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
// Initialization on page load
// ==============================================================================
document.addEventListener("DOMContentLoaded", () => {
  loadStats();
  loadEventsTable();
});
