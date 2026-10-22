package com.openfactu.databridge.core.connector;

/** Operaciones que un conector declara soportar. El motor comprueba la capacidad antes de usarla. */
public enum Capability {
    /** Verificar que la conexión es posible. */
    TEST_CONNECTION,
    /** Listar conjuntos de datos y describir su esquema. */
    DISCOVER,
    /** Leer registros por lotes. */
    READ,
    /** Insertar registros. */
    WRITE,
    /** Insertar o actualizar registros según una clave. */
    UPSERT
}
