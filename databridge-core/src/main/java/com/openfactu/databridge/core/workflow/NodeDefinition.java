package com.openfactu.databridge.core.workflow;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Nodo de un flujo tal como se declara. La forma de {@code config} depende de {@code type} y la
 * valida el tipo de nodo correspondiente en el motor.
 *
 * @param id identificador único dentro del flujo
 * @param type tipo de nodo, p. ej. {@code read}, {@code map}, {@code write}
 * @param config configuración propia del tipo (valores JSON: mapas, listas, textos, números...)
 */
public record NodeDefinition(String id, String type, Map<String, Object> config) {

    public NodeDefinition {
        config = config == null ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(config));
    }
}
