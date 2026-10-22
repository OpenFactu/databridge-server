package com.openfactu.databridge.core.connector;

import com.openfactu.databridge.core.connection.ConnectionDefinition;
import com.openfactu.databridge.core.connection.SecretResolver;

/** Crea conectores para un tipo de conexión (p. ej. {@code postgresql}). */
public interface ConnectorProvider {

    /** Tipo de conexión que atiende, igual a {@link ConnectionDefinition#type()}. */
    String type();

    DataConnector create(ConnectionDefinition connection, SecretResolver secrets);
}
