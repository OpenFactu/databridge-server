package com.openfactu.databridge.core.connector;

import com.openfactu.databridge.core.error.ConfigurationException;
import com.openfactu.databridge.core.schema.DataSchema;
import java.util.List;
import java.util.Set;

/**
 * Conector a una tecnología concreta (JDBC, REST, archivos...), nunca a una aplicación concreta.
 *
 * <p>Cada operación corresponde a una {@link Capability}. Las operaciones no declaradas en {@link
 * #capabilities()} lanzan {@link ConfigurationException}.
 */
public interface DataConnector extends AutoCloseable {

    Set<Capability> capabilities();

    default boolean supports(Capability capability) {
        return capabilities().contains(capability);
    }

    /** @throws com.openfactu.databridge.core.error.ConnectivityException si no se puede conectar */
    default void testConnection() {
        throw unsupported(Capability.TEST_CONNECTION);
    }

    default List<DatasetRef> listDatasets() {
        throw unsupported(Capability.DISCOVER);
    }

    default DataSchema describe(DatasetRef dataset) {
        throw unsupported(Capability.DISCOVER);
    }

    default RecordReader openReader(ReadRequest request) {
        throw unsupported(Capability.READ);
    }

    default RecordWriter openWriter(WriteRequest request) {
        throw unsupported(request.mode() == WriteMode.UPSERT ? Capability.UPSERT : Capability.WRITE);
    }

    @Override
    default void close() {}

    private ConfigurationException unsupported(Capability capability) {
        return new ConfigurationException(getClass().getSimpleName() + " no soporta la capacidad " + capability + ".");
    }
}
