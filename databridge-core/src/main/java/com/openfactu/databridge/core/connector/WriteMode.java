package com.openfactu.databridge.core.connector;

/** Semántica de escritura en el destino. */
public enum WriteMode {
    /** Solo inserta; un registro existente provoca un fallo. */
    INSERT,
    /** Inserta o actualiza según las claves indicadas; repetir la escritura no duplica registros. */
    UPSERT
}
