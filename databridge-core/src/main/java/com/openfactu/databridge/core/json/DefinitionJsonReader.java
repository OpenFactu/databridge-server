package com.openfactu.databridge.core.json;

import com.networknt.schema.Schema;
import com.networknt.schema.SchemaRegistry;
import com.networknt.schema.SpecificationVersion;
import com.openfactu.databridge.core.connection.ConnectionDefinition;
import com.openfactu.databridge.core.connection.SecretRef;
import com.openfactu.databridge.core.error.ConfigurationException;
import com.openfactu.databridge.core.error.ValidationException;
import com.openfactu.databridge.core.validation.ValidationIssue;
import com.openfactu.databridge.core.workflow.WorkflowDefinition;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import tools.jackson.core.JacksonException;
import tools.jackson.core.TokenStreamLocation;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Lee definiciones JSON (flujos y conexiones) y las convierte en modelos del core.
 *
 * <p>Pasos: sintaxis JSON → versión de formato → JSON Schema (forma, campos obligatorios, tipos) →
 * records. La coherencia entre elementos (aristas, ciclos, tipos de nodo) se valida después con
 * {@link com.openfactu.databridge.core.workflow.WorkflowValidator}.
 */
public final class DefinitionJsonReader {

    private final JsonMapper mapper = JsonMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .build();

    private final Schema workflowSchema;
    private final Schema connectionSchema;

    public DefinitionJsonReader() {
        SchemaRegistry registry = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12);
        this.workflowSchema = loadSchema(registry, "workflow-v1.schema.json");
        this.connectionSchema = loadSchema(registry, "connection-v1.schema.json");
    }

    public WorkflowDefinition readWorkflow(Path file) {
        return read(file, in -> readWorkflow(in, file.toString()));
    }

    public WorkflowDefinition readWorkflow(InputStream json, String sourceName) {
        JsonNode tree = parseTree(json, sourceName);
        JsonNode formatVersion = tree.get("formatVersion");
        if (formatVersion == null || !formatVersion.canConvertToInt()) {
            throw new ConfigurationException("Falta 'formatVersion' (entero) en el flujo " + sourceName);
        }
        if (formatVersion.asInt() != WorkflowDefinition.CURRENT_FORMAT_VERSION) {
            throw new ConfigurationException("formatVersion " + formatVersion.asInt() + " no soportada en " + sourceName
                    + "; esta versión de DataBridge lee la " + WorkflowDefinition.CURRENT_FORMAT_VERSION);
        }
        validateSchema(tree, workflowSchema, "El flujo " + sourceName);
        return bind(tree, sourceName, WorkflowDefinition.class);
    }

    public ConnectionDefinition readConnection(Path file) {
        return read(file, in -> readConnection(in, file.toString()));
    }

    public ConnectionDefinition readConnection(InputStream json, String sourceName) {
        JsonNode tree = parseTree(json, sourceName);
        validateSchema(tree, connectionSchema, "La conexión " + sourceName);
        ConnectionDocument document = bind(tree, sourceName, ConnectionDocument.class);
        SecretRef credentials = document.credentials() == null ? null : SecretRef.parse(document.credentials());
        return new ConnectionDefinition(document.id(), document.type(), document.parameters(), credentials);
    }

    private JsonNode parseTree(InputStream json, String sourceName) {
        try {
            JsonNode tree = mapper.readTree(json);
            if (tree == null || !tree.isObject()) {
                throw new ConfigurationException("Se esperaba un objeto JSON en " + sourceName);
            }
            return tree;
        } catch (JacksonException e) {
            throw invalidJson(sourceName, e);
        }
    }

    private static void validateSchema(JsonNode tree, Schema schema, String subject) {
        List<ValidationIssue> issues = schema.validate(tree).stream()
                .map(error -> new ValidationIssue(
                        jsonPointer(error.getInstanceLocation().toString()), error.getMessage()))
                .toList();
        if (!issues.isEmpty()) {
            throw new ValidationException(subject, issues);
        }
    }

    /** Las rutas de los errores de esquema son JSON Pointer; la raíz se muestra como "/". */
    private static String jsonPointer(String location) {
        return location.isEmpty() ? "/" : location;
    }

    private <T> T bind(JsonNode tree, String sourceName, Class<T> type) {
        try {
            return mapper.treeToValue(tree, type);
        } catch (JacksonException e) {
            throw invalidJson(sourceName, e);
        }
    }

    private static ConfigurationException invalidJson(String sourceName, JacksonException e) {
        return new ConfigurationException(
                "JSON inválido en " + sourceName + location(e) + ": " + e.getOriginalMessage(), e);
    }

    private static String location(JacksonException e) {
        TokenStreamLocation location = e.getLocation();
        if (location == null || location.getLineNr() < 0) {
            return "";
        }
        return " (línea " + location.getLineNr() + ", columna " + location.getColumnNr() + ")";
    }

    private static Schema loadSchema(SchemaRegistry registry, String resource) {
        try (InputStream in = DefinitionJsonReader.class.getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalStateException("Falta el recurso de esquema " + resource);
            }
            return registry.getSchema(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static <T> T read(Path file, Function<InputStream, T> reader) {
        if (!Files.isRegularFile(file)) {
            throw new ConfigurationException("No existe el archivo de definición " + file.toAbsolutePath());
        }
        try (InputStream in = Files.newInputStream(file)) {
            return reader.apply(in);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer " + file, e);
        }
    }

    /** Forma JSON de una conexión: las credenciales llegan como texto {@code env:NOMBRE}. */
    private record ConnectionDocument(String id, String type, Map<String, String> parameters, String credentials) {}
}
