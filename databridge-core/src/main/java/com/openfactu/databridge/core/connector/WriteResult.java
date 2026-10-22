package com.openfactu.databridge.core.connector;

/** Contadores de una escritura. */
public record WriteResult(long inserted, long updated, long failed) {

    public static final WriteResult EMPTY = new WriteResult(0, 0, 0);

    public WriteResult {
        if (inserted < 0 || updated < 0 || failed < 0) {
            throw new IllegalArgumentException("Los contadores no pueden ser negativos");
        }
    }

    public WriteResult plus(WriteResult other) {
        return new WriteResult(inserted + other.inserted, updated + other.updated, failed + other.failed);
    }
}
