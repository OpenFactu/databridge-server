package com.openfactu.databridge.core.validation;

import java.util.Objects;

/**
 * Un problema concreto de validación.
 *
 * @param path ubicación del problema dentro de la definición, p. ej. {@code nodes[2].type}
 * @param message explicación accionable de qué está mal y cómo corregirlo
 */
public record ValidationIssue(String path, String message) {

    public ValidationIssue {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(message, "message");
    }

    @Override
    public String toString() {
        return path + ": " + message;
    }
}
