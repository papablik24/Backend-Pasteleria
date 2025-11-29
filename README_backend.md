# Backend - Pastelería

Este proyecto es un backend en Java (Spring Boot) para la aplicación `pasteleria-main`.

## Tecnologías usadas
- Java 17
- Spring Boot (web, data-jpa, security)
- MySQL
- Lombok
- springdoc-openapi (Swagger UI)

## Cómo ejecutar
1. Asegúrate que MySQL esté corriendo y crea la base de datos `pasteleria_db` (o ajusta `application.properties`).

   ```sql
   CREATE DATABASE pasteleria_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. Ajusta usuario/clave en `backend/src/main/resources/application.properties`.
3. Desde la carpeta `backend` ejecutar:

   ```powershell
   mvn spring-boot:run
   ```

4. Swagger UI estará disponible en `http://localhost:8081/swagger-ui/index.html` (puede variar según versión).

## Endpoints principales
- `POST /api/auth/register` - Registrar usuario. JSON: `{ "username": "u", "password": "p", "role": "ROLE_ADMIN" }` (role opcional, por defecto `ROLE_USER`).
- `GET /api/auth/me` - Información del usuario autenticado (usa HTTP Basic).
- `GET /api/products` - Listar productos.
- `GET /api/products/{id}` - Obtener producto.
- `POST /api/products` - Crear producto (requiere `ROLE_ADMIN`).
- `PUT /api/products/{id}` - Actualizar producto (requiere `ROLE_ADMIN`).
- `DELETE /api/products/{id}` - Eliminar producto (requiere `ROLE_ADMIN`).

## Seguridad
- Autenticación HTTP Basic con usuarios almacenados en MySQL. Passwords con `BCrypt`.
- Rutas públicas: `/api/auth/**`, `/v3/api-docs/**`, `/swagger-ui/**`.

- Autenticación basada en JWT: endpoint `/api/v1/auth/login` devuelve un token JWT que debe usarse como `Authorization: Bearer <token>` en llamadas protegidas.
- `BCrypt` se usa para almacenar passwords de forma segura.

## Mapeo con la rúbrica (Resumido)
- Uso de MySQL: la aplicación persiste entidades (`User`, `Product`, `Order`) en MySQL usando Spring Data JPA.
- Documentación API: integrada con `springdoc-openapi` (Swagger UI).
- Uso de Spring Boot starters: `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-security`.
- Seguridad: Spring Security con `UserDetailsService` y `BCrypt` para almacenamiento seguro de contraseñas.
- JPA y Lombok: entidades usan anotaciones JPA y Lombok para reducir boilerplate.
- Documentación entregada: este README + Swagger + comentarios en código.

Si quieres, puedo:
- Añadir JWT en vez de HTTP Basic.
- Añadir más endpoints (carrito, checkout) y tests.
- Crear Dockerfile y `docker-compose` con MySQL.
