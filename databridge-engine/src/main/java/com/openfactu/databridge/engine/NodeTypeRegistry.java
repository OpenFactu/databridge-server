package com.openfactu.databridge.engine;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * Tipos de nodo que este motor sabe ejecutar. En la Fase 3 cada tipo registrará además su
 * implementación; por ahora solo se usa para rechazar flujos con tipos desconocidos.
 */
public final class NodeTypeRegistry {

    private final Set<String> types = new TreeSet<>();

    public NodeTypeRegistry register(String type) {
        if (!types.add(type)) {
            throw new IllegalArgumentException("El tipo de nodo '" + type + "' ya está registrado");
        }
        return this;
    }

    public boolean contains(String type) {
        return types.contains(type);
    }

    public Set<String> types() {
        return Collections.unmodifiableSet(types);
    }
}
