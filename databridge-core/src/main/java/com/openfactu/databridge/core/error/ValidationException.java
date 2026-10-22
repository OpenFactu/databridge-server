package com.openfactu.databridge.core.error;

import com.openfactu.databridge.core.validation.ValidationIssue;
import java.util.List;
import java.util.stream.Collectors;

/** Una definición o unos datos no superan la validación; incluye todos los problemas encontrados. */
public final class ValidationException extends DataBridgeException {

    private static final long serialVersionUID = 1L;

    // transient: las excepciones no se serializan con Java serialization en DataBridge.
    private final transient List<ValidationIssue> issues;

    public ValidationException(String subject, List<ValidationIssue> issues) {
        super(buildMessage(subject, issues));
        this.issues = List.copyOf(issues);
    }

    public List<ValidationIssue> issues() {
        return issues;
    }

    private static String buildMessage(String subject, List<ValidationIssue> issues) {
        return issues.stream()
                .map(issue -> "  - " + issue)
                .collect(Collectors.joining(
                        System.lineSeparator(), subject + " no es válido:" + System.lineSeparator(), ""));
    }
}
