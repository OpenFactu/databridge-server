package com.openfactu.databridge.core.connection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.openfactu.databridge.core.error.ValidationException;
import com.openfactu.databridge.core.validation.ValidationIssue;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ConnectionValidatorTest {

    private final ConnectionValidator validator = new ConnectionValidator();

    @Test
    void acceptsValidConnection() {
        var connection = new ConnectionDefinition(
                "pg-source", "postgresql", Map.of("host", "localhost"), SecretRef.env("PG_PASSWORD"));

        assertThat(validator.validate(connection)).isEmpty();
    }

    @Test
    void rejectsSecretsWrittenAsParameters() {
        var connection = new ConnectionDefinition("pg-source", "postgresql", Map.of("password", "1234"), null);

        assertThat(validator.validate(connection))
                .extracting(ValidationIssue::path)
                .containsExactly("parameters.password");
    }

    @Test
    void reportsAllInvalidIdentifiers() {
        var connection = new ConnectionDefinition("1bad", "", Map.of(), null);

        assertThatThrownBy(() -> validator.validateOrThrow(connection))
                .isInstanceOfSatisfying(
                        ValidationException.class,
                        e -> assertThat(e.issues())
                                .extracting(ValidationIssue::path)
                                .containsExactly("id", "type"));
    }
}
