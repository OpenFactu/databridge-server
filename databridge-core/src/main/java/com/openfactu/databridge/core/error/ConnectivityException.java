package com.openfactu.databridge.core.error;

/** No se pudo conectar o comunicar con un sistema externo. */
public final class ConnectivityException extends DataBridgeException {

    private static final long serialVersionUID = 1L;

    public ConnectivityException(String message) {
        super(message);
    }

    public ConnectivityException(String message, Throwable cause) {
        super(message, cause);
    }
}
