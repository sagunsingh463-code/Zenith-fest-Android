from typing import Dict, Optional
from pydantic import BaseModel, Field


class EventOut(BaseModel):
    """Event representation returned in listings and summary views."""
    id: int
    title: str
    category: str
    description: Optional[str] = None
    event_date: str
    event_time: str
    venue: str
    capacity: int
    fee: int
    icon: Optional[str] = None
    available_seats: int


class EventDetail(EventOut):
    """Detailed event representation (shares shape with EventOut in Zenith '26)."""
    pass


class RegistrationIn(BaseModel):
    """Input payload sent by frontend or client to register for an event."""
    student_name: str = Field(..., description="Full name of student")
    roll_number: str = Field(..., description="College roll number")
    email: str = Field(..., description="Student email address")
    phone: Optional[str] = Field(default=None, description="Optional phone number")


class RegistrationOut(BaseModel):
    """Confirmation payload returned upon successful registration."""
    id: int
    event_id: int
    student_name: str
    registered_at: str


class RegistrationItemOut(BaseModel):
    """Registration record with event title, used in admin registration list."""
    id: int
    event_id: int
    event_title: str
    student_name: str
    roll_number: str
    email: str
    phone: Optional[str] = None
    registered_at: str


class StatsOut(BaseModel):
    """Live statistics computed for admin dashboard."""
    total_events: int
    total_registrations: int
    total_capacity: int
    overall_utilization_pct: float
    upcoming_events_count: int
    events_by_category: Dict[str, int]


class EventCreateIn(BaseModel):
    """Payload to create a new event (Stretch goal)."""
    title: str
    category: str
    description: Optional[str] = ""
    event_date: str
    event_time: str
    venue: str
    capacity: int = Field(gt=0, description="Capacity must be greater than 0")
    fee: int = Field(default=0, ge=0, description="Fee in INR, 0 for free")
    icon: Optional[str] = "🎯"
