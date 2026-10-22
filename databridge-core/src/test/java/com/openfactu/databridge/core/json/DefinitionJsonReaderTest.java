package com.openfactu.databridge.core.json;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.openfactu.databridge.core.connection.SecretRef;
import com.openfactu.databridge.core.error.ConfigurationException;
import com.openfactu.databridge.core.error.ValidationException;
import com.openfactu.databridge.core.validation.ValidationIssue;
import com.openfactu.databridge.core.workflow.EdgeDefinition;
import com.openfactu.databridge.core.workflow.NodeDefinition;
import com.openfactu.databridge.core.workflow.WorkflowValidator;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DefinitionJsonReaderTest {

    private final DefinitionJsonReader reader = new DefinitionJsonReader();

    @Test
    void readsExampleWorkflow() {
        var workflow = reader.readWorkflow(resource("/workflows/customers-sync.json"), "customers-sync.json");

        assertThat(workflow.id()).isEqualTo("customers-sync");
        assertThat(workflow.nodes()).extracting(NodeDefinition::type).containsExactly("read", "map", "write");
        assertThat(workflow.edges())
                .containsExactly(
                        new EdgeDefinition("read-customers", "map-fields"),
                        new EdgeDefinition("map-fields", "write-clients"));
        assertThat(workflow.nodes().get(1).config())
                .containsEntry("fields", Map.of("external_id", "id", "name", "full_name", "email_address", "email"));
        assertThat(new WorkflowValidator(Set.of("read", "map", "write")).validate(workflow))
                .isEmpty();
    }

    @Test
    void readsConnectionWithSecretReference() {
        var connection = reader.readConnection(resource("/connections/pg-source.json"), "pg-source.json");

        assertThat(connection.type()).isEqualTo("postgresql");
        assertThat(connection.parameters()).containsEntry("database", "crm");
        assertThat(connection.credentialsRef()).contains(SecretRef.env("DATABRIDGE_PG_PASSWORD"));
    }

    @Test
    void rejectsLiteralCredentialsViaSchema() {
        String json = """
                {"id": "pg", "type": "postgresql", "credentials": "hunter2"}
                """;

        assertThatThrownBy(() -> reader.readConnection(stream(json), "inline"))
                .isInstanceOfSatisfying(
                        ValidationException.class,
                        e -> assertThat(e.issues())
                                .extracting(ValidationIssue::path)
                                .containsExactly("/credentials"));
    }

    @Test
    void reportsSchemaViolationsWithPaths() {
        String json = """
                {"formatVersion": 1, "id": "w", "version": 1,
                 "nodes": [{"id": "a"}], "unexpected": true}
                """;

        assertThatThrownBy(() -> reader.readWorkflow(stream(json), "inline"))
                .isInstanceOfSatisfying(
                        ValidationException.class,
                        e -> assertThat(e.issues())
                                .extracting(ValidationIssue::path)
                                .containsExactlyInAnyOrder("/", "/nodes/0"));
    }

    @Test
    void rejectsUnsupportedFormatVersion() {
        String json = """
                {"formatVersion": 99, "id": "w", "version": 1, "nodes": []}
                """;

        assertThatThrownBy(() -> reader.readWorkflow(stream(json), "inline"))
                .isInstanceOf(ConfigurationException.class)
                .hasMessageContaining("formatVersion 99");
    }

    @Test
    void reportsSyntaxErrorLocation() {
        assertThatThrownBy(() -> reader.readWorkflow(stream("{\n  \"id\": }"), "broken.json"))
                .isInstanceOf(ConfigurationException.class)
                .hasMessageContaining("broken.json")
                .hasMessageContaining("línea 2");
    }

    private static InputStream resource(String path) {
        return DefinitionJsonReaderTest.class.getResourceAsStream(path);
    }

    private static InputStream stream(String json) {
        return new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
    }
}
