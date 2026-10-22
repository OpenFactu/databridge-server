package com.openfactu.databridge.core.execution;

import com.openfactu.databridge.core.connector.WriteResult;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Resultado de una ejecución de flujo.
 *
 * @param executionId identificador de esta ejecución
 * @param workflowId flujo ejecutado
 * @param workflowVersion versión del flujo ejecutada
 * @param status estado final
 * @param startedAt inicio
 * @param finishedAt fin
 * @param recordsRead registros leídos de los orígenes
 * @param written contadores de escritura acumulados
 * @param errors mensajes de error accionables (sin secretos ni valores de registros)
 */
public record ExecutionResult(
        String executionId,
        String workflowId,
        int workflowVersion,
        ExecutionStatus status,
        Instant startedAt,
        Instant finishedAt,
        long recordsRead,
        WriteResult written,
        List<String> errors) {

    public ExecutionResult {
        Objects.requireNonNull(executionId, "executionId");
        Objects.requireNonNull(workflowId, "workflowId");
        Objects.requireNonNull(status, "status");
        written = written == null ? WriteResult.EMPTY : written;
        errors = errors == null ? List.of() : List.copyOf(errors);
    }
}
