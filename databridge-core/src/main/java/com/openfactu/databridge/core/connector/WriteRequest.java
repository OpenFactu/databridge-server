package com.openfactu.databridge.core.connector;

import java.util.List;
import java.util.Objects;

/**
 * Petición de escritura.
 *
 * @param dataset conjunto de datos de destino
 * @param mode semántica de escritura
 * @param keyFields campos que identifican un registro; obligatorios en {@link WriteMode#UPSERT}
 */
public record WriteRequest(DatasetRef dataset, WriteMode mode, List<String> keyFields) {

    public WriteRequest {
        Objects.requireNonNull(dataset, "dataset");
        Objects.requireNonNull(mode, "mode");
        keyFields = keyFields == null ? List.of() : List.copyOf(keyFields);
        if (mode == WriteMode.UPSERT && keyFields.isEmpty()) {
            throw new IllegalArgumentException("UPSERT requiere al menos un campo clave");
        }
    }
}
