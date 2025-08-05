# Configuración de MySQL para EvtNet Backend

## Requisitos previos
- Docker y Docker Compose instalados
- Java 21
- Maven

## Pasos para levantar la base de datos

### 1. Levantar MySQL con Docker
```bash
docker-compose up -d
```

### 2. Verificar que MySQL esté funcionando
```bash
docker-compose ps
```

### 3. Ver logs de MySQL
```bash
docker-compose logs mysql
```

### 4. Conectarse a MySQL (opcional)
```bash
docker exec -it evtnet-mysql mysql -u evtnet_user -p
# Contraseña: evtnet_pass
```

## Configuración de la aplicación

La aplicación Spring Boot está configurada para conectarse automáticamente a MySQL con los siguientes parámetros:

- **Host**: localhost
- **Puerto**: 3306
- **Base de datos**: evtnet_db
- **Usuario**: evtnet_user
- **Contraseña**: evtnet_pass

## Comandos útiles

### Detener MySQL
```bash
docker-compose down
```

### Detener y eliminar volúmenes (cuidado: elimina todos los datos)
```bash
docker-compose down -v
```

### Reiniciar MySQL
```bash
docker-compose restart mysql
```

## Estructura de archivos

```
evtnet-back/
├── docker-compose.yml          # Configuración de Docker
├── mysql/
│   └── init/                   # Scripts de inicialización
└── src/main/resources/
    └── application.properties  # Configuración de la aplicación
```

## Notas importantes

1. **Persistencia de datos**: Los datos se guardan en un volumen de Docker llamado `mysql_data`
2. **Puerto**: MySQL está expuesto en el puerto 3306
3. **Seguridad**: Cambia las contraseñas en producción
4. **JPA**: La aplicación está configurada con `ddl-auto=update` para crear/actualizar tablas automáticamente 