import sys
import os
from pathlib import Path
from fastapi import Request

# Add project root directory to sys.path
ROOT_DIR = Path(__file__).resolve().parent.parent
if str(ROOT_DIR) not in sys.path:
    sys.path.insert(0, str(ROOT_DIR))

from backend.main import app

@app.middleware("http")
async def debug_path_middleware(request: Request, call_next):
    response = await call_next(request)
    response.headers["X-Debug-Path"] = request.url.path
    response.headers["X-Debug-Raw"] = str(request.scope.get("raw_path", b""))
    return response
