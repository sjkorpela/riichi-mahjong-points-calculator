#!/bin/bash
set -e

echo "Starting backend..."
cd /app/backend && java -jar app.jar &
BACKEND_PID=$!
cd /app

echo "Starting frontend..."
cd /app/frontend && HOSTNAME=0.0.0.0 PORT=${PORT:-3000} node server.js &
FRONTEND_PID=$!

wait -n $BACKEND_PID $FRONTEND_PID
EXIT_CODE=$?

kill $BACKEND_PID $FRONTEND_PID 2>/dev/null
exit $EXIT_CODE