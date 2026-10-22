package com.openfactu.databridge.core.connection;

import java.util.Map;
import java.util.Optional;

/**
 * Definición declarativa de una conexión a un origen o destino.
 *
 * @param id identificador único de la conexión, referenciado desde los flujos
 * @param type tipo de conector por tecnología, p. ej. {@code postgresql} o {@code sqlserver}
 * @param parameters parámetros no secretos (host, puerto, base de datos...)
 * @param credentials referencia opcional al secreto (contraseña, token); puede ser {@code null}
 */
public record ConnectionDefinition(String id, String type, Map<String, String> parameters, SecretRef credentials) {

    public ConnectionDefinition {
        parameters = parameters == null ? Map.of() : Map.copyOf(parameters);
    }

    public Optional<SecretRef> credentialsRef() {
        return Optional.ofNullable(credentials);
    }
}
