# DataBridge

Motor de integración de datos **local-first** y multiplataforma (Windows y Linux). Conecta orígenes y
destinos (bases de datos, APIs y, más adelante, archivos), transforma registros y ejecuta flujos
definidos de forma declarativa en JSON. El motor de flujos es propio (Java puro); no se basa en Apache
Camel, Spring Integration ni n8n.

## Qué es

- Un catálogo de **conexiones** a orígenes y destinos, con secretos referenciados (nunca escritos en los JSON).
- Un **motor** que interpreta definiciones de flujos versionadas (grafo dirigido acíclico de nodos).
- Bloques reutilizables para leer, mapear, filtrar, validar y escribir datos **por lotes**.
- Un ejecutor con historial, errores y reintentos seguros (se incorporan progresivamente).

Los conectores se implementan **por protocolo o tecnología** (JDBC, REST...), nunca por pareja de
aplicaciones. No hay clases del tipo "Odoo → SQL Server".

## Qué no es (por ahora)

- Un ERP, un sistema de facturación ni un ETL en la nube.
- Un generador de integraciones sin configuración.
- Sincronización bidireccional ni CDC en tiempo real.
- Una vía para escribir en tablas internas de un ERP cuando existe una API de negocio oficial.

## Estado

Fase 0 (base del proyecto) y esqueleto de la Fase 1 (contratos del core). Todavía **no** hay
conectores reales, ejecución de flujos ni API. Ver [docs/architecture.md](docs/architecture.md) y la
hoja de ruta.

## Requisitos

- **JDK 21** (por ejemplo Eclipse Temurin 21). Gradle no hace falta: se usa el wrapper incluido.
- Windows 10/11 x64 o Linux x64.

Gradle 9 necesita un JDK 17 o superior para arrancar. El código se compila siempre con Java 21 mediante
*toolchains*: si `JAVA_HOME` apunta a otra versión pero hay un JDK
21 instalado, lo usará para compilar.

## Comandos

| Acción | Linux | Windows (cmd / PowerShell) |
|---|---|---|
| Compilar, comprobar formato y ejecutar tests | `./gradlew build` | `gradlew.bat build` |
| Solo tests | `./gradlew test` | `gradlew.bat test` |
| Tests de un módulo | `./gradlew :databridge-core:test` | `gradlew.bat :databridge-core:test` |
| Formatear el código | `./gradlew spotlessApply` | `gradlew.bat spotlessApply` |
| Arrancar el servidor | `./gradlew :databridge-server:bootRun` | `gradlew.bat :databridge-server:bootRun` |
| Ejecutar el jar | `java -jar databridge-server/build/libs/databridge-server-0.1.0-SNAPSHOT.jar` | ídem |

Los informes de tests quedan en `<módulo>/build/reports/tests/test/index.html`.

## Módulos

| Módulo | Contenido | Depende de |
|---|---|---|
| `databridge-core` | Modelos y contratos: conexiones, esquemas, registros, conectores, flujos, errores, lectura JSON | — (sin Spring) |
| `databridge-engine` | Runtime propio de flujos (por ahora: registro de tipos de nodo y carga validada) | core |
| `databridge-connectors` | Conectores por tecnología (JDBC en la Fase 2) | core |
| `databridge-infrastructure` | Secretos, persistencia SQLite (Fase 4), logs | core |
| `databridge-server` | Ensamblaje Spring Boot y, más adelante, API de administración | todos |

Reservados, aún sin crear: `databridge-tests/` (integración end-to-end) y `apps/databridge-studio/`
(Angular + Tauri).

## Ejemplo de definiciones

Flujo (`formatVersion` 1), validado con JSON Schema y después estructuralmente (ids, aristas, ciclos,
tipos de nodo conocidos):

```json
{
  "formatVersion": 1,
  "id": "customers-sync",
  "version": 1,
  "nodes": [
    { "id": "read-customers", "type": "read",
      "config": { "connection": "pg-source", "dataset": "public.customers", "batchSize": 500 } },
    { "id": "map-fields", "type": "map",
      "config": { "fields": { "external_id": "id", "name": "full_name", "email_address": "email" } } },
    { "id": "write-clients", "type": "write",
      "config": { "connection": "mssql-target", "dataset": "dbo.clients", "mode": "upsert", "keys": ["external_id"] } }
  ],
  "edges": [
    { "from": "read-customers", "to": "map-fields" },
    { "from": "map-fields", "to": "write-clients" }
  ]
}
```

Conexión: los secretos solo se referencian (`env:NOMBRE_VARIABLE`); un secreto literal se rechaza.

```json
{
  "id": "pg-source",
  "type": "postgresql",
  "parameters": { "host": "localhost", "port": "5432", "database": "crm", "user": "databridge" },
  "credentials": "env:DATABRIDGE_PG_PASSWORD"
}
```

## Licencia

Ver [LICENSE](LICENSE).
