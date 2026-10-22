package com.openfactu.databridge.core.connector;

import com.openfactu.databridge.core.record.DataRecord;
import com.openfactu.databridge.core.schema.DataSchema;
import java.util.List;

/**
 * Cursor de lectura por lotes. Mantiene recursos abiertos (conexión, cursor) hasta {@link #close()};
 * úsese siempre con try-with-resources.
 */
public interface RecordReader extends AutoCloseable {

    /** Esquema de los registros que devuelve este lector. */
    DataSchema schema();

    /**
     * Siguiente lote, con como mucho {@code batchSize} registros. Una lista vacía indica que no hay más
     * datos; las llamadas posteriores siguen devolviendo una lista vacía.
     */
    List<DataRecord> nextBatch();

    @Override
    void close();
}
