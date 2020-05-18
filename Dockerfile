FROM node:20.13-alpine as frontend

WORKDIR /
COPY frontend/package*.json ./app/

RUN npm --prefix app install
COPY frontend app
RUN npm run --prefix app build


FROM maven:3.9.14-eclipse-temurin-17 AS build

WORKDIR /

COPY backend/src /home/app/src
COPY backend/vendor-maven-repository /home/app/vendor-maven-repository
COPY backend/pom.xml /home/app
