package com.openfactu.databridge.core.error;

/** Fallo al escribir datos en un destino. */
public final class WriteException extends DataBridgeException {

    private static final long serialVersionUID = 1L;

    public WriteException(String message) {
        super(message);
    }

    public WriteException(String message, Throwable cause) {
        super(message, cause);
    }
}
