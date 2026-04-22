# ── Stage 1: Build backend ────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS backend-build

WORKDIR /build
COPY backend/pom.xml .
RUN mvn dependency:go-offline -q

COPY backend/src ./src
RUN mvn clean package -DskipTests -q


# ── Stage 2: Build frontend ───────────────────────────────────────────────────
FROM node:20-alpine AS frontend-build

WORKDIR /build
COPY frontend/package*.json .
RUN npm ci --silent

COPY frontend/ .
RUN npm run build


# ── Stage 3: Runtime ──────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-jammy

# Install Node.js
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && curl -fsSL https://deb.nodesource.com/setup_20.x | bash - \
    && apt-get install -y --no-install-recommends nodejs \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Backend
COPY --from=backend-build /build/target/RiichiPointsCalculator-1.0.jar backend/app.jar
COPY --from=backend-build /build/src/main/resources/yaku.json backend/src/main/resources/yaku.json

# Frontend
COPY --from=frontend-build /build/.next/standalone ./frontend/
COPY --from=frontend-build /build/.next/static ./frontend/.next/static
COPY --from=frontend-build /build/public ./frontend/public

COPY start.sh .
RUN chmod +x start.sh

ENV HOSTNAME=0.0.0.0
EXPOSE ${PORT:-3000}
ENTRYPOINT ["./start.sh"]