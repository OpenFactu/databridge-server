package com.openfactu.databridge.core.schema;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Estructura de un conjunto de datos (tabla, vista, recurso de API...).
 *
 * @param name nombre del conjunto de datos
 * @param fields campos en orden; los nombres son únicos
 */
public record DataSchema(String name, List<FieldSchema> fields) {

    public DataSchema {
        fields = List.copyOf(fields);
        Set<String> seen = new HashSet<>();
        for (FieldSchema field : fields) {
            if (!seen.add(field.name())) {
                throw new IllegalArgumentException(
                        "Campo duplicado '" + field.name() + "' en el esquema '" + name + "'");
            }
        }
    }

    public Optional<FieldSchema> field(String fieldName) {
        return fields.stream().filter(f -> f.name().equals(fieldName)).findFirst();
    }

    public List<FieldSchema> keyFields() {
        return fields.stream().filter(FieldSchema::key).toList();
    }
}
