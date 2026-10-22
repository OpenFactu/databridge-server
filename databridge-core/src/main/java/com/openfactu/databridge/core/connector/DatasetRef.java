package com.openfactu.databridge.core.connector;

/**
 * Identifica un conjunto de datos dentro de una conexión (tabla, vista, recurso...).
 *
 * @param namespace agrupación opcional, p. ej. el esquema SQL; puede ser {@code null}
 * @param name nombre del conjunto de datos
 */
public record DatasetRef(String namespace, String name) {

    public DatasetRef {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre del conjunto de datos no puede estar vacío");
        }
    }

    public static DatasetRef of(String name) {
        return new DatasetRef(null, name);
    }

    @Override
    public String toString() {
        return namespace == null ? name : namespace + "." + name;
    }
}
