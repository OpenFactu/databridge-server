package com.openfactu.databridge.core.record;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Registro genérico e inmutable: campos con nombre, en orden, que admiten valores nulos.
 *
 * <p>Los valores usan las clases Java documentadas en {@link
 * com.openfactu.databridge.core.schema.LogicalType}.
 */
public final class DataRecord {

    private final Map<String, Object> values;

    private DataRecord(Map<String, Object> values) {
        this.values = Collections.unmodifiableMap(values);
    }

    /** Crea un registro copiando el mapa dado (se respeta su orden de iteración). */
    public static DataRecord of(Map<String, ?> values) {
        Map<String, Object> copy = new LinkedHashMap<>();
        values.forEach((name, value) -> copy.put(Objects.requireNonNull(name, "field name"), value));
        return new DataRecord(copy);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Set<String> fieldNames() {
        return values.keySet();
    }

    public boolean has(String field) {
        return values.containsKey(field);
    }

    /** Valor del campo, o {@code null} si es nulo. Falla si el campo no existe. */
    public Object get(String field) {
        if (!values.containsKey(field)) {
            throw new IllegalArgumentException("El registro no tiene el campo '" + field + "'");
        }
        return values.get(field);
    }

    /** Valor del campo con el tipo esperado, o {@code null} si es nulo. */
    public <T> T get(String field, Class<T> type) {
        Object value = get(field);
        if (value != null && !type.isInstance(value)) {
            throw new IllegalArgumentException(
                    "El campo '" + field + "' es " + value.getClass().getSimpleName() + ", no " + type.getSimpleName());
        }
        return type.cast(value);
    }

    /** Vista inmutable de los valores. */
    public Map<String, Object> asMap() {
        return values;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof DataRecord record && values.equals(record.values);
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    @Override
    public String toString() {
        // Solo nombres de campo: los valores pueden contener datos personales y no deben acabar en logs.
        return "DataRecord" + values.keySet();
    }

    /** Constructor incremental de registros. */
    public static final class Builder {

        private final Map<String, Object> values = new LinkedHashMap<>();

        private Builder() {}

        public Builder set(String field, Object value) {
            values.put(Objects.requireNonNull(field, "field name"), value);
            return this;
        }

        public DataRecord build() {
            return new DataRecord(new LinkedHashMap<>(values));
        }
    }
}
