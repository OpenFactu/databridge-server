package com.openfactu.databridge.core.validation;

import java.util.regex.Pattern;

/**
 * Política de identificadores de DataBridge (ids de flujos, nodos, conexiones y tipos).
 *
 * <p>Son identificadores internos de configuración, no nombres SQL: los nombres de tablas y columnas
 * se validan y escapan en cada dialecto de conector.
 */
public final class Identifiers {

    public static final String RULE =
            "debe empezar por una letra y contener solo letras ASCII, dígitos, '_' o '-'" + " (máximo 64 caracteres)";

    private static final Pattern PATTERN = Pattern.compile("[A-Za-z][A-Za-z0-9_-]{0,63}");

    private Identifiers() {}

    public static boolean isValid(String value) {
        return value != null && PATTERN.matcher(value).matches();
    }
}
