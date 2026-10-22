# ADR 0001 — Stack y versiones iniciales

Fecha: 2026-10-22 · Estado: aceptada

## Decisión

| Componente | Versión | Motivo |
|---|---|---|
| Java | 21 LTS | Decisión del propietario del proyecto (en lugar de 25). Records, pattern matching y text blocks disponibles. |
| Gradle | 9.8.2 (wrapper, con `distributionSha256Sum`) | Última estable; soporta Java 21 y Spring Boot 4. |
| Spring Boot | 4.1.1 | Última GA. Solo en `databridge-server`. Su BOM se usa en todos los módulos **solo como catálogo de versiones** (Jackson, JUnit, AssertJ, SLF4J), sin añadir dependencias de Spring. |
| Jackson | 3.x (`tools.jackson`) | La línea que usa Spring Boot 4; evita tener Jackson 2 y 3 a la vez. |
| JSON Schema | `com.networknt:json-schema-validator` 3.0.8 | Mantenida, soporta draft 2020-12 y Jackson 3. Arrastra Jackson 3.2.x (Boot gestiona 3.1.x; Gradle resuelve la mayor, compatible a nivel de minor). |
| Tests | JUnit (vía BOM de Boot) + AssertJ | Testcontainers se añadirá en la Fase 2, cuando haya conectores. |
| Formato | Spotless 8 + palantir-java-format | Formato automático (`spotlessApply`) comprobado en `build`. |
| Compilador | `-Xlint:all,-processing -Werror -parameters` | Avisos como errores desde el principio. |

## Notas operativas

- `gradle.properties` añade `--add-exports jdk.compiler/...` al daemon: palantir-java-format usa el
  parser interno de javac.
- El *configuration cache* de Gradle está desactivado: con Spotless + palantir provocaba fallos
  intermitentes (`NoClassDefFoundError` de clases de palantir al reutilizar el daemon). Se puede
  reevaluar al actualizar Spotless.
- Se usa `buildSrc` con el plugin de convenciones `databridge.java-conventions` y un catálogo
  `gradle/libs.versions.toml`.

## Alternativas descartadas

- **Java 25:** propuesta inicial; se mantiene 21 por decisión del propietario. Migrar es cambiar
  `java` en el catálogo y en CI.
- **`subprojects {}` en el build raíz:** acopla todos los módulos; los plugins de convención son el
  enfoque recomendado por Gradle.
- **Librería JSON Schema de Everit/Justify:** Everit está sin mantenimiento activo; networknt es la
  opción mantenida con soporte de Jackson 3.
