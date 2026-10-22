package com.openfactu.databridge.core.connector;

import com.openfactu.databridge.core.record.DataRecord;
import java.util.List;

/**
 * Escritor por lotes. Mantiene recursos abiertos hasta {@link #close()}; úsese con try-with-resources.
 * La transaccionalidad es por lote: cada llamada a {@link #write} se confirma o falla de forma
 * independiente.
 */
public interface RecordWriter extends AutoCloseable {

    WriteResult write(List<DataRecord> batch);

    @Override
    void close();
}
