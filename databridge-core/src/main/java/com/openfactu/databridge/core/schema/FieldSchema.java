package com.openfactu.databridge.core.schema;

import java.util.Objects;

/**
 * Descripción de un campo.
 *
 * @param name nombre del campo tal como lo expone el sistema de origen
 * @param type tipo lógico
 * @param nullable si admite valores nulos
 * @param key si forma parte de la clave que identifica el registro
 */
public record FieldSchema(String name, LogicalType type, boolean nullable, boolean key) {

    public FieldSchema {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre de campo no puede estar vacío");
        }
        Objects.requireNonNull(type, "type");
    }
}
