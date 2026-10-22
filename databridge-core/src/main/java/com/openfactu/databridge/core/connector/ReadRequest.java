package com.openfactu.databridge.core.connector;

import java.util.List;
import java.util.Objects;

/**
 * Petición de lectura por lotes.
 *
 * @param dataset conjunto de datos a leer
 * @param fields campos a leer; vacío significa todos
 * @param batchSize número máximo de registros por lote
 */
public record ReadRequest(DatasetRef dataset, List<String> fields, int batchSize) {

    public ReadRequest {
        Objects.requireNonNull(dataset, "dataset");
        fields = fields == null ? List.of() : List.copyOf(fields);
        if (batchSize < 1) {
            throw new IllegalArgumentException("batchSize debe ser mayor que 0");
        }
    }
}
