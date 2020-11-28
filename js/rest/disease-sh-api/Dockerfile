FROM node:alpine

# WORKDIR create the directory and then execute cd
WORKDIR /home/container

COPY ../package-original.json ./package-lock.json ./
RUN npm ci

COPY src .
