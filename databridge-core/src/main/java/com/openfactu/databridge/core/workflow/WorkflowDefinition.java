package com.openfactu.databridge.core.workflow;

import java.util.List;

/**
 * Definición declarativa y versionada de un flujo: un grafo dirigido de nodos.
 *
 * <p>Este record solo transporta datos; la coherencia del grafo la comprueba {@link
 * WorkflowValidator}, que informa de todos los problemas a la vez.
 *
 * @param formatVersion versión del formato de definición (no del flujo); ver {@link
 *     #CURRENT_FORMAT_VERSION}
 * @param id identificador del flujo
 * @param version versión del flujo, incrementada por quien lo edita
 * @param description descripción opcional
 * @param nodes nodos del flujo
 * @param edges aristas entre nodos
 */
public record WorkflowDefinition(
        int formatVersion,
        String id,
        int version,
        String description,
        List<NodeDefinition> nodes,
        List<EdgeDefinition> edges) {

    public static final int CURRENT_FORMAT_VERSION = 1;

    public WorkflowDefinition {
        nodes = nodes == null ? List.of() : List.copyOf(nodes);
        edges = edges == null ? List.of() : List.copyOf(edges);
    }
}
