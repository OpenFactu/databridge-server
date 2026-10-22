package com.openfactu.databridge.core.schema;

/**
 * Tipos lógicos independientes de la tecnología. Cada conector traduce sus tipos nativos a estos y
 * define la clase Java con la que representa cada valor.
 */
public enum LogicalType {
    /** {@link String}. */
    STRING,
    /** {@link Integer}. */
    INTEGER,
    /** {@link Long}. */
    LONG,
    /** {@link java.math.BigDecimal}. */
    DECIMAL,
    /** {@link Double}. */
    DOUBLE,
    /** {@link Boolean}. */
    BOOLEAN,
    /** {@link java.time.LocalDate}. */
    DATE,
    /** {@link java.time.LocalDateTime} (sin zona). */
    TIMESTAMP,
    /** {@link java.time.OffsetDateTime} (con desplazamiento). */
    TIMESTAMP_TZ,
    /** {@code byte[]}. */
    BINARY,
    /** Tipo nativo sin correspondencia; el valor se transporta tal cual lo entrega el conector. */
    UNKNOWN
}
