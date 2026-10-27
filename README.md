# BigoBooks Ventas

Microservicio de ventas (`sales-service`). Ademas de la API REST, incluye cache
compartido en Redis para las lecturas por identificador.

## Cache

- **Proveedor:** Redis compartido, configurado con `spring-boot-starter-data-redis`
  y `spring-boot-starter-cache`.
- **Caches:** `orders`, `coupons` y `promotions`.
- **Claves:** `bigobooks:v1:sales:<cache>::<id>`.
  Ejemplo: `bigobooks:v1:sales:orders::42`.
- **TTL:** pedidos 10 minutos, cupones 30 minutos, promociones 15 minutos.
- **Politica persistente:** no hay caches persistentes en Ventas. Todos los datos
  son mutables y se invalidan por TTL; no se crean caches no declarados.
- **Que se cachea:** solo los DTOs publicos (`OrderDto`, `CouponDto`,
  `PromotionDto`) de `GET /orders/{id}`, `GET /coupons/{id}` y
  `GET /promotions/{id}`.
- **Que no se cachea:** listados paginados ni entidades JPA.
- **Invalidacion:** las escrituras de pedidos, cupones y promociones eliminan la
  clave afectada mediante `@CacheEvict`.
- **Tolerancia a fallos:** si Redis falla, el error se registra y la peticion
  continua contra PostgreSQL.
- **Logs:** cada consulta se registra como `CACHE HIT` o `CACHE MISS`; las
  escrituras y eliminaciones se registran como `CACHE PUT`, `CACHE EVICT` y
  `CACHE CLEAR` con el logger `com.bigobooks.cache`.

## Configuracion

La URL real de Redis se guarda en `src/main/resources/config/private.properties`
(ignorado por Git):

```properties
redis.url=rediss://usuario:password@host:6379
redis.ssl.enabled=true
```

El resto de la politica esta en `application.properties`:

```properties
spring.cache.type=redis
spring.data.redis.url=${redis.url}
sales.cache.key-prefix=bigobooks:v1:sales:
sales.cache.ttl.orders=10m
sales.cache.ttl.coupons=30m
sales.cache.ttl.promotions=15m
```

Para una instalacion nueva, copiar `config/private.properties.example` a
`config/private.properties` y completar `redis.url`.

## Build y tests

```powershell
.\mvnw.cmd -B clean test
```

Los tests que requieren base de datos real estan condicionados a
`-Dsales.test.full=true`.
