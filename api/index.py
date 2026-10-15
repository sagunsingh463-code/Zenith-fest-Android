import sys
import os
from pathlib import Path

# Add project root directory to sys.path
ROOT_DIR = Path(__file__).resolve().parent.parent
if str(ROOT_DIR) not in sys.path:
    sys.path.insert(0, str(ROOT_DIR))

from backend.main import app as fastapi_app

class VercelPathFixMiddleware:
    """
    ASGI middleware for Vercel deployment:
    Ensures that if Vercel routes a request as /api/index.py,
    the original matched path from the x-matched-path header is restored.
    """
    def __init__(self, app):
        self.app = app

    async def __call__(self, scope, receive, send):
        if scope.get("type") == "http":
            path = scope.get("path", "")
            if path in ("/api/index.py", "/api/index", "/api"):
                headers = dict(scope.get("headers", []))
                matched = headers.get(b"x-matched-path", b"").decode("utf-8", errors="ignore")
                if matched:
                    clean_matched = matched.split("?")[0]
                    scope["path"] = clean_matched
                    scope["raw_path"] = clean_matched.encode("utf-8")
        await self.app(scope, receive, send)

app = VercelPathFixMiddleware(fastapi_app)
