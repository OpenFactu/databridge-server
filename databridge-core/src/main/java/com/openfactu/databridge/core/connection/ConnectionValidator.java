package com.openfactu.databridge.core.connection;

import com.openfactu.databridge.core.error.ValidationException;
import com.openfactu.databridge.core.validation.Identifiers;
import com.openfactu.databridge.core.validation.ValidationIssue;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Validación estructural de {@link ConnectionDefinition}, independiente del tipo de conector. */
public final class ConnectionValidator {

    /** Fragmentos de nombre de parámetro que indican un secreto escrito en claro. */
    private static final Set<String> SECRET_HINTS =
            Set.of("password", "passwd", "secret", "token", "apikey", "api_key");

    public List<ValidationIssue> validate(ConnectionDefinition connection) {
        List<ValidationIssue> issues = new ArrayList<>();
        if (!Identifiers.isValid(connection.id())) {
            issues.add(new ValidationIssue("id", "'" + connection.id() + "' " + Identifiers.RULE));
        }
        if (!Identifiers.isValid(connection.type())) {
            issues.add(new ValidationIssue("type", "'" + connection.type() + "' " + Identifiers.RULE));
        }
        connection.parameters().keySet().stream().sorted().forEach(name -> {
            String lower = name.toLowerCase(Locale.ROOT);
            if (SECRET_HINTS.stream().anyMatch(lower::contains)) {
                issues.add(new ValidationIssue(
                        "parameters." + name,
                        "parece un secreto; usa 'credentials' con una referencia como 'env:NOMBRE_VARIABLE'"));
            }
        });
        return issues;
    }

    public void validateOrThrow(ConnectionDefinition connection) {
        List<ValidationIssue> issues = validate(connection);
        if (!issues.isEmpty()) {
            throw new ValidationException("La conexión '" + connection.id() + "'", issues);
        }
    }
}
