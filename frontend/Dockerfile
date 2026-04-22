FROM node:20-alpine AS build
 
WORKDIR /build
COPY package*.json .
RUN npm ci --silent
 
COPY . .
RUN npm run build
 
 
FROM node:20-alpine
 
WORKDIR /app
COPY --from=build /build/.next/standalone ./
COPY --from=build /build/.next/static ./.next/static
COPY --from=build /build/public ./public
ENV HOSTNAME=0.0.0.0
EXPOSE 3000
ENTRYPOINT ["node", "server.js"]