## Build stage

FROM node:20-alpine3.18 as build-stage
WORKDIR /app
COPY package.json ./
RUN npm install
COPY . .
ARG NODE_ENV
RUN if [ "$NODE_ENV" = "dev" ] ; then \
      npm run build ; \
    else \
      npm run build ;  \
    fi


## Production stage

FROM nginx:stable-alpine as production-stage
RUN mkdir /app
COPY --from=build-stage /app/dist /app
