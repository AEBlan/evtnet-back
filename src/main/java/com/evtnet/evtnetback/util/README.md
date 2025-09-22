# RegistroSingleton

Usar inyección de dependencias para acceder al RegistroSingleton.

## Write

``` java
    write(String registro, String tipo, String subtipo, String descripcion);

    write(String registro, String tipo, String subtipo, String descripcion, String username);
```

Escribe al log indicado por `registro` una entrada con el `tipo`, `subtipo` y `descripcion` indicados.

Si se especifica el `username`, también se usará este; caso contrario, será el usuario con la sesión iniciada que ejecutó el endpoint.

El `tipo` y el `subtipo` deben corresponder al `registro`.

Se genera un archivo de registro por día y por tipo de registro, en `/app/storage/logs/{registro}/{yyyy}-{MM}-{dd}.csv`.

## getReader

``` java
    getReader(String registro, LocalDateTime fechaDesde, LocalDateTime fechaHasta)
```

Genera una instancia de RegistroReader del `registro` indicado, leyendo solamente los registros entre `fechaDesde` y `fechaHasta`.

### RegistroReader

* tipos(String[] valores): deja solo las entradas cuyos tipos se encuentran en esta lista. Si está vacía, se ignora este filtro.
* subtipos(String[] valores): deja solo las entradas cuyos subtipos se encuentran en esta lista. Si está vacía, se ignora este filtro.
* usuarios(String[] valores): deja solo las entradas cuyos usernames se encuentran en esta lista. Si está vacía, se ignora este filtro.
* read(): filtra por tipos, subtipos y usuarios, devolviendo una `List<DTORegistro>`.
