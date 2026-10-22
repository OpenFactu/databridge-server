package com.openfactu.databridge.engine;

import com.openfactu.databridge.core.json.DefinitionJsonReader;
import com.openfactu.databridge.core.workflow.WorkflowDefinition;
import com.openfactu.databridge.core.workflow.WorkflowValidator;
import java.nio.file.Path;

/** Carga un flujo desde JSON y lo valida contra los tipos de nodo registrados en el motor. */
public final class WorkflowLoader {

    private final DefinitionJsonReader reader;
    private final NodeTypeRegistry registry;

    public WorkflowLoader(DefinitionJsonReader reader, NodeTypeRegistry registry) {
        this.reader = reader;
        this.registry = registry;
    }

    /**
     * @throws com.openfactu.databridge.core.error.ConfigurationException si el archivo no se puede leer
     * @throws com.openfactu.databridge.core.error.ValidationException si el flujo no es válido
     */
    public WorkflowDefinition load(Path file) {
        WorkflowDefinition workflow = reader.readWorkflow(file);
        new WorkflowValidator(registry.types()).validateOrThrow(workflow);
        return workflow;
    }
}
