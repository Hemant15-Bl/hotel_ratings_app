# Stage 1: Build the React app
FROM node:20-alpine AS build

WORKDIR /app

COPY package*.json ./

RUN npm install

COPY . .

RUN npm run build

# Stage 2: Server the app with Nginx
FROM nginx:stable-alpine

# Copy our custom config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy built files from stage 1
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

CMD [ "nginx", "-g", "daemon off;" ]