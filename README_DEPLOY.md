# Guía de Despliegue en Render

## Requisitos Previos
1. Cuenta en [Render](https://render.com) (gratis)
2. Repositorio de GitHub con tu código

## Pasos para Desplegar

### 1. Subir el Proyecto a GitHub
```bash
# En la carpeta raíz del proyecto
git init
git add .
git commit -m "Initial commit - Tinkhec Demo"
git branch -M main
git remote add origin https://github.com/TU_USUARIO/TU_REPOSITORIO.git
git push -u origin main
```

### 2. Configurar en Render

1. Ve a [Render Dashboard](https://dashboard.render.com/)
2. Haz clic en **"New +"** → **"Web Service"**
3. Conecta tu repositorio de GitHub
4. Selecciona el repositorio `TinkhecDemo`

### 3. Configuración del Servicio

Usa estos valores:

- **Name**: `tinkhec-demo` (o el nombre que prefieras)
- **Region**: Selecciona la más cercana a ti
- **Branch**: `main`
- **Root Directory**: (dejar vacío o poner `main`)
- **Runtime**: `Java`
- **Build Command**: 
  ```
  cd main && mvn clean install -DskipTests
  ```
- **Start Command**: 
  ```
  cd main && java -Dspring.profiles.active=prod -jar target/main-0.0.1-SNAPSHOT.jar
  ```

### 4. Variables de Entorno (Optional)

En la sección "Environment Variables", agrega:

- `SPRING_PROFILES_ACTIVE` = `prod`
- `JAVA_OPTS` = `-Xmx512m -Xms256m`

### 5. Configuración Adicional

- **Instance Type**: Selecciona "Free" para empezar
- **Auto-Deploy**: Activa esta opción para deploy automático en cada push

### 6. Desplegar

1. Haz clic en **"Create Web Service"**
2. Render comenzará a construir y desplegar tu aplicación
3. El proceso puede tardar 5-10 minutos la primera vez
4. Una vez completado, recibirás una URL como: `https://tinkhec-demo.onrender.com`

## Acceso a la Aplicación

- **URL de Login**: `https://tu-app.onrender.com/login`
- **Usuario de prueba**: `admin` / `admin123` (según tu configuración)

## ⚠️ Notas Importantes

### Base de Datos H2 (In-Memory)
- Los datos se **resetean cada vez que la aplicación se reinicia**
- Para datos persistentes, necesitarás migrar a PostgreSQL
- Render ofrece PostgreSQL gratuito que puedes conectar

### Plan Gratuito de Render
- La aplicación **se duerme después de 15 minutos de inactividad**
- El primer request después de dormir tardará ~30 segundos
- Para mantenerla activa 24/7, necesitas el plan de pago ($7/mes)

### Actualizar la Aplicación
Simplemente haz `git push` a tu repositorio:
```bash
git add .
git commit -m "Descripción de cambios"
git push
```

Render detectará los cambios y re-desplegará automáticamente.

## Migrar a PostgreSQL (Recomendado para Producción)

### 1. Crear Base de Datos en Render
1. En Render Dashboard → **"New +"** → **"PostgreSQL"**
2. Crea la base de datos (plan gratuito disponible)
3. Copia la **Internal Database URL**

### 2. Actualizar `pom.xml`
Agrega la dependencia de PostgreSQL:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

### 3. Actualizar `application-prod.properties`
```properties
spring.datasource.url=${DATABASE_URL}
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
```

### 4. Agregar Variable de Entorno en Render
- Nombre: `DATABASE_URL`
- Valor: La Internal Database URL de tu PostgreSQL

## Solución de Problemas

### La aplicación no inicia
- Revisa los logs en Render Dashboard → Tu servicio → "Logs"
- Verifica que el Build Command y Start Command sean correctos

### Error de memoria
- Aumenta `JAVA_OPTS`: `-Xmx768m -Xms512m`
- O considera actualizar al plan de pago

### Cambios no se reflejan
- Asegúrate de hacer commit y push a GitHub
- Verifica que Auto-Deploy esté activado
- Puedes forzar un re-deploy en Render Dashboard → "Manual Deploy"

## Soporte

Para más información:
- [Render Java Docs](https://render.com/docs/deploy-spring-boot)
- [Render Community](https://community.render.com/)
