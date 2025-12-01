# Guía de Deployment en Render.com

Esta guía te ayudará a desplegar la aplicación Clínica Veterinaria en Render.com con PostgreSQL.

## Requisitos Previos

1. Cuenta en [Render.com](https://render.com)
2. Repositorio en GitHub con el código del proyecto
3. Archivo `.env` con las variables de entorno necesarias (ver más abajo)

## Paso 1: Crear Base de Datos PostgreSQL

1. Ingresa a tu dashboard de Render
2. Haz clic en "New +" y selecciona "PostgreSQL"
3. Configura la base de datos:
   - **Name**: `clinica-veterinaria-db`
   - **Database**: `clinica_veterinaria`
   - **User**: (se genera automáticamente)
   - **Region**: Selecciona la más cercana
   - **Plan**: Free (para desarrollo) o Starter/Professional (para producción)
4. Haz clic en "Create Database"
5. Espera a que se cree la base de datos (toma unos minutos)
6. **IMPORTANTE**: Guarda las credenciales:
   - Internal Database URL
   - External Database URL
   - Username
   - Password

## Paso 2: Configurar Web Service (Backend)

1. En el dashboard de Render, haz clic en "New +" y selecciona "Web Service"
2. Conecta tu repositorio de GitHub
3. Configura el servicio:
   - **Name**: `clinica-veterinaria-backend`
   - **Region**: La misma que la base de datos
   - **Branch**: `master` (o la rama que desees)
   - **Root Directory**: (dejar en blanco si el backend está en la raíz)
   - **Environment**: `Java`
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -Dserver.port=$PORT -Dspring.profiles.active=render -jar target/clinica_veternica-0.0.1-SNAPSHOT.jar`
   - **Plan**: Free (para desarrollo) o Starter/Professional (para producción)

## Paso 3: Configurar Variables de Entorno

En la sección "Environment" del Web Service, agrega las siguientes variables:

### Variables Obligatorias:

```
# Base de datos (Render la provee automáticamente)
DATABASE_URL = <Internal Database URL de tu PostgreSQL en Render>

# JWT Configuration
JWT_SECRET = tu-clave-secreta-super-segura-de-al-menos-256-bits
JWT_EXPIRATION = 86400000

# Spring Profile
SPRING_PROFILES_ACTIVE = render
```

### Variables Opcionales (Email):

```
MAIL_HOST = smtp.gmail.com
MAIL_PORT = 587
MAIL_USERNAME = tu-email@gmail.com
MAIL_PASSWORD = tu-app-password-de-gmail
MAIL_FROM_NAME = Clínica Veterinaria
```

### Generación de JWT_SECRET:

Puedes generar una clave JWT segura con:

```bash
# En Linux/Mac:
openssl rand -base64 64

# O en Node.js:
node -e "console.log(require('crypto').randomBytes(64).toString('base64'))"
```

## Paso 4: Deploy

1. Haz clic en "Create Web Service"
2. Render comenzará a construir y desplegar tu aplicación
3. El proceso puede tomar 5-10 minutos la primera vez
4. Una vez completado, verás el estado como "Live"
5. Tu aplicación estará disponible en: `https://clinica-veterinaria-backend.onrender.com`

## Paso 5: Verificar el Deployment

1. Accede a: `https://tu-app.onrender.com/swagger-ui.html`
2. Deberías ver la interfaz de Swagger UI
3. Prueba algunos endpoints para verificar que funciona correctamente

## Paso 6: Deploy del Frontend (Opcional)

Si deseas desplegar el frontend en Render:

1. Haz clic en "New +" y selecciona "Static Site"
2. Configura:
   - **Name**: `clinica-veterinaria-frontend`
   - **Build Command**: `cd frontend && npm install && npm run build`
   - **Publish Directory**: `frontend/dist`
3. Agrega la variable de entorno:
   ```
   VITE_API_URL = https://tu-backend.onrender.com
   ```

## Configuración de CORS

Si el frontend está en un dominio diferente, asegúrate de configurar CORS en `SecurityConfig.java`:

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList(
        "https://tu-frontend.onrender.com",
        "http://localhost:5173"
    ));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

## Troubleshooting

### Error: "Application failed to respond"
- Verifica que el puerto se esté leyendo de la variable `$PORT`
- Revisa los logs en el dashboard de Render
- Asegúrate de que `SPRING_PROFILES_ACTIVE=render` esté configurado

### Error de conexión a la base de datos
- Verifica que `DATABASE_URL` esté correctamente configurada
- Asegúrate de usar el "Internal Database URL" (no el External)
- Verifica que la base de datos esté en estado "Available"

### La aplicación se duerme (Free Tier)
- En el plan Free, Render suspende las aplicaciones después de 15 minutos de inactividad
- La primera solicitud después de la suspensión puede tardar 30-60 segundos
- Considera upgradearte a un plan de pago si necesitas disponibilidad 24/7

### Cambiar de MySQL a PostgreSQL en desarrollo local

Si quieres usar PostgreSQL localmente:

1. Instala PostgreSQL
2. Crea la base de datos:
   ```sql
   CREATE DATABASE clinica_veterinaria_dev;
   ```
3. La configuración por defecto en `application.properties` ya está lista para PostgreSQL

Si prefieres seguir usando MySQL localmente, descomenta las líneas correspondientes en `application.properties`.

## Docker con PostgreSQL (Desarrollo Local)

Para probar con PostgreSQL localmente usando Docker:

```bash
# Iniciar con PostgreSQL
docker-compose -f docker-compose.postgres.yml up -d

# Ver logs
docker-compose -f docker-compose.postgres.yml logs -f backend

# Detener
docker-compose -f docker-compose.postgres.yml down
```

## Migraciones de Base de Datos

Hibernate está configurado con `spring.jpa.hibernate.ddl-auto=update`, lo que significa que:
- Las tablas se crearán automáticamente en el primer deploy
- Los cambios en las entidades se aplicarán automáticamente

**IMPORTANTE**: Para producción, considera usar:
- `ddl-auto=validate` después del primer deploy
- Herramientas de migración como Flyway o Liquibase

## Monitoreo

Render proporciona:
- Logs en tiempo real en el dashboard
- Métricas de CPU y memoria
- Health checks automáticos

Puedes acceder al endpoint de health check:
```
https://tu-app.onrender.com/actuator/health
```

## Backup de Base de Datos

Render hace backups automáticos en planes de pago. Para planes Free:
1. Exporta los datos regularmente usando pg_dump
2. O usa el botón "Manual Backup" en el dashboard de la base de datos

## Costos Estimados

### Plan Free:
- **Web Service**: Gratis (con limitaciones de sleep)
- **PostgreSQL**: Gratis (750 horas/mes, 1GB RAM, 1GB storage)
- **Limitaciones**: Sleep después de 15min inactividad, 750 horas/mes

### Plan Starter (~$7-21/mes):
- **Web Service**: $7/mes (siempre activo)
- **PostgreSQL**: $7/mes (1GB RAM, 10GB storage, backups)
- **Total**: ~$14/mes

## Recursos Adicionales

- [Documentación de Render](https://render.com/docs)
- [Render Status](https://status.render.com/)
- [Community Forum](https://community.render.com/)
- [Spring Boot on Render](https://render.com/docs/deploy-spring-boot)

## Soporte

Si tienes problemas:
1. Revisa los logs en el dashboard de Render
2. Verifica las variables de entorno
3. Consulta la documentación oficial
4. Contacta al soporte de Render (solo planes de pago)
