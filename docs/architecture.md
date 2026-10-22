# Arquitectura

## Capas y reglas de dependencia

```
databridge-server  (Spring Boot: ensamblaje, futura API)
   ├── databridge-engine          → core
   ├── databridge-connectors      → core
   ├── databridge-infrastructure  → core
   └── databridge-core            (sin Spring, sin drivers)
```

- **core** no depende de Spring ni de conectores concretos. Su única dependencia de terceros es
  Jackson + JSON Schema, confinada al paquete `core.json`.
- **engine** depende solo de core; nunca de drivers de bases de datos.
- **connectors** implementa `DataConnector`/`ConnectorProvider` de core.
- **server** es el único módulo que conoce Spring; crea los objetos del motor como beans explícitos
  (`EngineConfiguration`).
- Ningún módulo conoce conceptos de aplicaciones concretas (Odoo, SAP...).

## Paquetes de `databridge-core`

| Paquete | Contenido |
|---|---|
| `connection` | `ConnectionDefinition`, `SecretRef` (`env:NOMBRE`), `SecretResolver`, `ConnectionValidator` |
| `schema` | `LogicalType`, `FieldSchema`, `DataSchema` |
| `record` | `DataRecord` inmutable, ordenado y con nulos; `toString()` no expone valores |
| `connector` | `Capability`, `DataConnector`, `RecordReader`/`RecordWriter` (`AutoCloseable`, por lotes), `ReadRequest`, `WriteRequest`, `WriteResult`, `ConnectorProvider` |
| `workflow` | `WorkflowDefinition`, `NodeDefinition`, `EdgeDefinition`, `WorkflowValidator` |
| `execution` | `ExecutionStatus`, `ExecutionResult` |
| `error` | `DataBridgeException` y sus subtipos: `Configuration`, `Connectivity`, `Validation`, `Write` |
| `json` | `DefinitionJsonReader` y los esquemas JSON (`workflow-v1`, `connection-v1`) |

## Decisiones de diseño

- **Records como transporte, validadores aparte.** Las definiciones (`WorkflowDefinition`,
  `ConnectionDefinition`) solo normalizan colecciones; `WorkflowValidator`/`ConnectionValidator`
  devuelven *todos* los problemas como `ValidationIssue(path, message)` en lugar de fallar en el
  primero. Los modelos producidos por código (`DataSchema`, `ReadRequest`...) sí validan en el
  constructor porque un valor inválido ahí es un error de programación.
- **Validación JSON en dos niveles.** JSON Schema (draft 2020-12) cubre forma, tipos, campos
  obligatorios y propiedades desconocidas; el validador Java cubre lo que el esquema no puede
  expresar bien (ids únicos, aristas a nodos existentes, ciclos, tipos de nodo registrados en el motor).
- **Lectura por lotes con cierre explícito.** `RecordReader.nextBatch()` devuelve una lista acotada
  por `batchSize` y una lista vacía al terminar; lectores y escritores son `AutoCloseable`. No se
  exponen `Stream` ni `ResultSet`.
- **Capacidades explícitas.** Un conector declara `Capability`s; las operaciones no soportadas fallan
  con `ConfigurationException` y un mensaje claro.
- **Excepciones unchecked** con una base común (`DataBridgeException`) para capturarlas en los límites
  de nodo/lote/ejecución.
- **`formatVersion`** separada de `version`: la primera versiona el formato de definición (cambia
  con DataBridge), la segunda el flujo concreto (cambia quien lo edita).

## Pendiente por fase

- **Fase 2:** núcleo JDBC, dialectos PostgreSQL/SQL Server (quoting, tipos, upsert), Testcontainers.
- **Fase 3:** `WorkflowNode`, `ExecutionContext`, nodos `read`/`map`/`filter`/`write`, ejecución por
  lotes, CLI.
- **Fase 4:** política de errores, persistencia SQLite de ejecuciones, idempotencia y checkpoints.
