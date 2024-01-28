FROM node:17-alpine as node
WORKDIR /app
COPY . .
RUN npm install
RUN npm run build --prod

FROM nginx:alpine
COPY --from=node /app/dist/web /usr/share/nginx/html
COPY /nginx-custom.conf /etc/nginx/conf.d/default.conf