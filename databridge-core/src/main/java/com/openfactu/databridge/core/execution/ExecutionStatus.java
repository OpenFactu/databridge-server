package com.openfactu.databridge.core.execution;

/** Estado de una ejecución de flujo. */
public enum ExecutionStatus {
    PENDING,
    RUNNING,
    SUCCEEDED,
    /** Terminó, pero algunos registros fallaron según la política de errores. */
    COMPLETED_WITH_ERRORS,
    FAILED,
    CANCELLED
}
