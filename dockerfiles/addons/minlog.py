from mitmproxy import http
import csv, os, time

LOG_DIR = os.getenv("MITM_LOG_DIR", "/logs")
os.makedirs(LOG_DIR, exist_ok=True)

LOG_FILE_NAME = os.getenv("MITM_LOG_FILE","minlog.csv")

LOG_FILE = os.path.join(LOG_DIR, LOG_FILE_NAME)

if not os.path.exists(LOG_FILE):
    with open(LOG_FILE, "w", newline="", encoding="utf-8") as f:
        writer = csv.writer(f, quoting=csv.QUOTE_NONNUMERIC)
        writer.writerow(["status_code", "path", "method","timestamp_seconds"])

def response(flow: http.HTTPFlow):
    row = [
        flow.response.status_code,
        flow.request.path,
        flow.request.method,
        int(time.time())
    ]
    with open(LOG_FILE, "a", newline="", encoding="utf-8") as f:
        writer = csv.writer(f, quoting=csv.QUOTE_NONNUMERIC)
        writer.writerow(row)
