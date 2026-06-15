FROM node:20.13-alpine as frontend

WORKDIR /
COPY frontend/package*.json ./app/

RUN npm --prefix app install
COPY frontend app
RUN npm run --prefix app build
