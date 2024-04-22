export type DockerfilePreset =
  | 'node'
  | 'bun'
  | 'java-jar'
  | 'python'
  | 'static-nginx'
  | 'go'
  | 'rust'
  | 'php-apache'
  | 'php-fpm'
  | 'dotnet'
  | 'custom'

export type ComposePreset =
  | 'blank'
  | 'node-web'
  | 'python-web'
  | 'java-app'
  | 'static-nginx'
  | 'proxy-app-postgres'

export type RestartPolicy = 'no' | 'always' | 'on-failure' | 'unless-stopped'

export type KeyValueEntry = {
  key: string
  value: string
}

export const dockerfilePresetDefaults: Record<
  DockerfilePreset,
  {
    base: string
    install: string[]
    build: string[]
    start: string
    port: number
    copyOutput?: string
    runtimeBase?: string
  }
> = {
  node: {
    base: 'node:20-alpine',
    install: ['npm ci'],
    build: ['npm run build'],
    start: 'npm run start',
    port: 3000,
  },
  bun: {
    base: 'oven/bun:1',
    install: ['bun install --frozen-lockfile'],
    build: ['bun run build'],
    start: 'bun run start',
    port: 3000,
  },
  'java-jar': {
    base: 'maven:3.9-eclipse-temurin-21',
    runtimeBase: 'eclipse-temurin:21-jre-alpine',
    install: [],
    build: ['./mvnw package -DskipTests'],
    start: 'java -jar app.jar',
    port: 8080,
    copyOutput: '/workspace/target/*.jar /app/app.jar',
  },
  python: {
    base: 'python:3.12-slim',
    install: ['pip install -r requirements.txt'],
    build: [],
    start: 'python app.py',
    port: 8000,
  },
  'static-nginx': {
    base: 'node:20-alpine',
    runtimeBase: 'nginx:1.27-alpine',
    install: ['npm ci'],
    build: ['npm run build'],
    start: 'nginx -g "daemon off;"',
    port: 80,
    copyOutput: '/workspace/dist /usr/share/nginx/html',
  },
  go: {
    base: 'golang:1.24-alpine',
    runtimeBase: 'alpine:3.21',
    install: ['go mod download'],
    build: ['go build -o app ./...'],
    start: './app',
    port: 8080,
    copyOutput: '/workspace/app /app/app',
  },
  rust: {
    base: 'rust:1.86-alpine',
    runtimeBase: 'alpine:3.21',
    install: ['cargo fetch'],
    build: ['cargo build --release'],
    start: './app',
    port: 8080,
    copyOutput: '/workspace/target/release/app /app/app',
  },
  'php-apache': {
    base: 'php:8.3-apache',
    install: [],
    build: [],
    start: 'apache2-foreground',
    port: 80,
  },
  'php-fpm': {
    base: 'php:8.3-fpm-alpine',
    install: [],
    build: [],
    start: 'php-fpm',
    port: 9000,
  },
  dotnet: {
    base: 'mcr.microsoft.com/dotnet/sdk:8.0',
    runtimeBase: 'mcr.microsoft.com/dotnet/aspnet:8.0',
    install: [],
    build: ['dotnet publish -c Release -o /workspace/out'],
    start: 'dotnet MyApp.dll',
    port: 8080,
    copyOutput: '/workspace/out /app',
  },
  custom: {
    base: 'ubuntu:24.04',
