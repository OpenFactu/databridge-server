package com.openfactu.databridge.core.error;

/**
 * Base de todas las excepciones propias de DataBridge.
 *
 * <p>Son unchecked: el motor las captura en los límites (nodo, lote, ejecución) para registrarlas,
 * y obligar a declararlas en cada firma no aportaría información adicional.
 */
public abstract class DataBridgeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    protected DataBridgeException(String message) {
        super(message);
    }

    protected DataBridgeException(String message, Throwable cause) {
        super(message, cause);
    }
}
