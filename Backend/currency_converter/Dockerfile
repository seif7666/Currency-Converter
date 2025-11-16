FROM redis:8.4-rc1-alpine3.22
LABEL authors="seif7666"
COPY src/Redis.conf /usr/local/etc/redis/redis.conf
EXPOSE 6379
CMD [ "redis-server", "/usr/local/etc/redis/redis.conf" ]
