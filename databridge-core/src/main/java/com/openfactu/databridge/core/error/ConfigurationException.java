package com.openfactu.databridge.core.error;

/** Definición o configuración inválida o incompleta (flujo, conexión, secreto). */
public final class ConfigurationException extends DataBridgeException {

    private static final long serialVersionUID = 1L;

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
