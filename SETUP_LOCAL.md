# Setup Local - Clínica Veterinaria

Guía para configurar el proyecto localmente con PostgreSQL.

## Requisitos

- Java 21 o superior
- Maven 3.8+
- PostgreSQL 12+ (o Docker)
- Node.js 18+ (para el frontend)

## Opción 1: PostgreSQL Local (Windows/Mac/Linux)

### 1. Instalar PostgreSQL

#### Windows:
1. Descarga el instalador desde [postgresql.org](https://www.postgresql.org/download/windows/)
2. Ejecuta el instalador (recomendado: PostgreSQL 16)
3. Durante la instalación:
   - Usuario: `postgres`
   - Password: `postgres` (o el que prefieras)
   - Puerto: `5432`

#### Mac (con Homebrew):
```bash
brew install postgresql@16
brew services start postgresql@16
```

#### Linux (Ubuntu/Debian):
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
```

### 2. Crear Base de Datos

#### Windows (desde psql o pgAdmin):
```sql
CREATE DATABASE clinica_veterinaria_dev;
```

#### Mac/Linux:
```bash
# Crear base de datos
createdb clinica_veterinaria_dev

# O usando psql:
psql -U postgres
CREATE DATABASE clinica_veterinaria_dev;
\q
```

### 3. Configurar Variables de Entorno

Copia el archivo `.env.example` a `.env`:

```bash
cp .env.example .env
```

Edita `.env` con tus credenciales de PostgreSQL:

```env
POSTGRES_DB=clinica_veterinaria_dev
POSTGRES_USER=postgres
POSTGRES_PASSWORD=tu-password-aqui

JWT_SECRET=tu-clave-jwt-aqui
JWT_EXPIRATION=86400000
```

### 4. Ejecutar la Aplicación

```bash
# Compilar el proyecto
./mvnw clean install

# Ejecutar
./mvnw spring-boot:run
```

La aplicación estará disponible en:
- Backend: http://localhost:8084
- Swagger UI: http://localhost:8084/swagger-ui.html

---

## Opción 2: Docker (Recomendado para Desarrollo)

Esta es la forma más fácil de tener todo funcionando sin instalar PostgreSQL localmente.

### 1. Asegúrate de tener Docker instalado

- Windows/Mac: [Docker Desktop](https://www.docker.com/products/docker-desktop)
- Linux: Docker Engine

### 2. Copiar variables de entorno

```bash
cp .env.example .env
```

Edita `.env` si necesitas cambiar las credenciales por defecto.

### 3. Levantar toda la infraestructura

```bash
# Levantar PostgreSQL, Backend y Frontend
docker-compose up -d

# Ver logs
docker-compose logs -f

# Ver solo logs del backend
docker-compose logs -f backend
```

### 4. Acceder a la aplicación

- Frontend: http://localhost
- Backend: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- PostgreSQL: localhost:5432

### 5. Comandos útiles

```bash
# Detener todo
docker-compose down

# Detener y eliminar volúmenes (resetea la BD)
docker-compose down -v

# Rebuild después de cambios
docker-compose up -d --build

# Ver estado de los contenedores
docker-compose ps

# Acceder a la base de datos
docker-compose exec postgres psql -U postgres -d clinica_veterinaria
```

---

## Opción 3: Solo Base de Datos en Docker + App Local

Si prefieres ejecutar solo PostgreSQL en Docker pero la app localmente:

### 1. Levantar solo PostgreSQL

```bash
docker-compose up -d postgres
```

### 2. La aplicación se conectará automáticamente

Las credenciales por defecto en `application.properties` ya apuntan a:
- Host: localhost
- Puerto: 5432
- Database: clinica_veterinaria_dev
- Usuario: postgres
- Password: postgres

### 3. Ejecutar la aplicación

```bash
./mvnw spring-boot:run
```

---

## Frontend (React + Vite)

### 1. Instalar dependencias

```bash
cd frontend
npm install
```

### 2. Ejecutar en desarrollo

```bash
npm run dev
```

El frontend estará disponible en: http://localhost:5173

### 3. Configurar URL del Backend

Edita `frontend/src/services/api.js` si el backend está en otro puerto:

```javascript
const API_BASE_URL = 'http://localhost:8084';
```

---

## Verificación de la Instalación

### 1. Verificar PostgreSQL

```bash
# Desde psql
psql -U postgres -d clinica_veterinaria_dev -c "\dt"

# Con Docker
docker-compose exec postgres psql -U postgres -d clinica_veterinaria -c "\dt"
```

Deberías ver las tablas creadas por Hibernate.

### 2. Verificar Backend

Accede a: http://localhost:8084/swagger-ui.html

Deberías ver la documentación de la API.

### 3. Probar Endpoints

```bash
# Health check
curl http://localhost:8084/actuator/health

# Debe responder: {"status":"UP"}
```

---

## Solución de Problemas

### Error: "Connection refused" o "could not connect to server"

**Problema**: PostgreSQL no está corriendo.

**Solución**:
```bash
# Windows (Services)
Busca "Services" → PostgreSQL → Start

# Mac
brew services start postgresql@16

# Linux
sudo systemctl start postgresql

# Docker
docker-compose up -d postgres
```

### Error: "database does not exist"

**Problema**: La base de datos no ha sido creada.

**Solución**:
```bash
createdb clinica_veterinaria_dev

# O con Docker:
docker-compose exec postgres psql -U postgres -c "CREATE DATABASE clinica_veterinaria;"
```

### Error: "password authentication failed"

**Problema**: Credenciales incorrectas.

**Solución**:
1. Verifica las credenciales en `application.properties`
2. O edita el archivo `.env`
3. O cambia la contraseña de PostgreSQL:

```bash
psql -U postgres
ALTER USER postgres PASSWORD 'nueva-password';
```

### Error: Port 5432 already in use

**Problema**: Ya tienes otra instancia de PostgreSQL corriendo.

**Solución**:
- Opción 1: Detén el PostgreSQL local y usa Docker
- Opción 2: Cambia el puerto en `docker-compose.yml`:
  ```yaml
  ports:
    - "5433:5432"  # Usa puerto 5433 externamente
  ```
  Y actualiza `application.properties`:
  ```properties
  spring.datasource.url=jdbc:postgresql://localhost:5433/clinica_veterinaria_dev
  ```

### Error: Hibernate dialect warning

**Problema**: Versión de PostgreSQL no reconocida.

**Solución**: Este warning es normal y no afecta la funcionalidad. PostgreSQL 12+ es soportado.

---

## Variables de Entorno Importantes

| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `SPRING_PROFILES_ACTIVE` | Perfil activo | `dev` |
| `POSTGRES_DB` | Nombre de la BD | `clinica_veterinaria` |
| `POSTGRES_USER` | Usuario PostgreSQL | `postgres` |
| `POSTGRES_PASSWORD` | Password PostgreSQL | `postgres` |
| `JWT_SECRET` | Clave secreta JWT | (ver .env.example) |
| `JWT_EXPIRATION` | Expiración token (ms) | `86400000` (24h) |

---

## Siguiente Paso: Deploy a Render

Una vez que todo funcione localmente, consulta `DEPLOYMENT_RENDER.md` para desplegar en producción.

---

## Recursos Adicionales

- [Documentación PostgreSQL](https://www.postgresql.org/docs/)
- [Spring Boot con PostgreSQL](https://spring.io/guides/gs/accessing-data-jpa/)
- [Docker Compose](https://docs.docker.com/compose/)
- [Render Deployment Guide](./DEPLOYMENT_RENDER.md)
