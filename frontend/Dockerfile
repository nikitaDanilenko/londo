ARG BUILD_PLATFORM=linux/amd64
FROM --platform=$BUILD_PLATFORM node:26-alpine AS build

WORKDIR /app

ENV NODE_OPTIONS=--openssl-legacy-provider

RUN npm install -g create-elm-app

COPY elm.json elmapp.config.js ./
COPY src/ src/
COPY public/ public/

ENV ELM_APP_GRAPH_QL_ENDPOINT=/graphql

RUN elm-app build

FROM nginx:alpine

COPY --from=build /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf