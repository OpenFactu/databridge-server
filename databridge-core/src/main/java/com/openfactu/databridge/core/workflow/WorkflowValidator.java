package com.openfactu.databridge.core.workflow;

import com.openfactu.databridge.core.error.ValidationException;
import com.openfactu.databridge.core.validation.Identifiers;
import com.openfactu.databridge.core.validation.ValidationIssue;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Validación estructural de un {@link WorkflowDefinition}: identificadores, tipos de nodo conocidos,
 * aristas coherentes y ausencia de ciclos. No valida la configuración interna de cada nodo.
 */
public final class WorkflowValidator {

    private final Set<String> knownNodeTypes;

    /** @param knownNodeTypes tipos de nodo que el motor sabe ejecutar */
    public WorkflowValidator(Set<String> knownNodeTypes) {
        this.knownNodeTypes = Set.copyOf(knownNodeTypes);
    }

    public List<ValidationIssue> validate(WorkflowDefinition workflow) {
        List<ValidationIssue> issues = new ArrayList<>();

        if (workflow.formatVersion() != WorkflowDefinition.CURRENT_FORMAT_VERSION) {
            issues.add(new ValidationIssue(
                    "formatVersion",
                    "versión de formato " + workflow.formatVersion() + " no soportada; usa "
                            + WorkflowDefinition.CURRENT_FORMAT_VERSION));
        }
        if (!Identifiers.isValid(workflow.id())) {
            issues.add(new ValidationIssue("id", "'" + workflow.id() + "' " + Identifiers.RULE));
        }
        if (workflow.version() < 1) {
            issues.add(new ValidationIssue("version", "debe ser un entero mayor o igual que 1"));
        }
        if (workflow.nodes().isEmpty()) {
            issues.add(new ValidationIssue("nodes", "el flujo debe tener al menos un nodo"));
        }

        Set<String> nodeIds = validateNodes(workflow.nodes(), issues);
        boolean edgesValid = validateEdges(workflow.edges(), nodeIds, issues);
        if (edgesValid) {
            Set<String> inCycle = nodesInCycles(nodeIds, workflow.edges());
            if (!inCycle.isEmpty()) {
                issues.add(new ValidationIssue(
                        "edges",
                        "el grafo contiene ciclos entre los nodos " + inCycle + "; los flujos deben ser acíclicos"));
            }
        }
        return issues;
    }

    public void validateOrThrow(WorkflowDefinition workflow) {
        List<ValidationIssue> issues = validate(workflow);
        if (!issues.isEmpty()) {
            throw new ValidationException("El flujo '" + workflow.id() + "'", issues);
        }
    }

    private Set<String> validateNodes(List<NodeDefinition> nodes, List<ValidationIssue> issues) {
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < nodes.size(); i++) {
            NodeDefinition node = nodes.get(i);
            String path = "nodes[" + i + "]";
            if (!Identifiers.isValid(node.id())) {
                issues.add(new ValidationIssue(path + ".id", "'" + node.id() + "' " + Identifiers.RULE));
            } else if (!ids.add(node.id())) {
                issues.add(new ValidationIssue(path + ".id", "id de nodo duplicado '" + node.id() + "'"));
            }
            if (node.type() == null || !knownNodeTypes.contains(node.type())) {
                issues.add(new ValidationIssue(
                        path + ".type",
                        "tipo de nodo desconocido '" + node.type() + "'; tipos disponibles: "
                                + new TreeSet<>(knownNodeTypes)));
            }
        }
        return ids;
    }

    private static boolean validateEdges(
            List<EdgeDefinition> edges, Set<String> nodeIds, List<ValidationIssue> issues) {
        int before = issues.size();
        Set<EdgeDefinition> seen = new HashSet<>();
        for (int i = 0; i < edges.size(); i++) {
            EdgeDefinition edge = edges.get(i);
            String path = "edges[" + i + "]";
            if (!nodeIds.contains(edge.from())) {
                issues.add(new ValidationIssue(path + ".from", "el nodo '" + edge.from() + "' no existe"));
            }
            if (!nodeIds.contains(edge.to())) {
                issues.add(new ValidationIssue(path + ".to", "el nodo '" + edge.to() + "' no existe"));
            }
            if (!seen.add(edge)) {
                issues.add(new ValidationIssue(path, "arista duplicada " + edge.from() + " -> " + edge.to()));
            }
        }
        return issues.size() == before;
    }

    /**
     * Algoritmo de Kahn: se eliminan repetidamente los nodos sin aristas de entrada. Los que quedan
     * forman parte de un ciclo o dependen de uno.
     */
    private static Set<String> nodesInCycles(Set<String> nodeIds, List<EdgeDefinition> edges) {
        Map<String, Integer> inDegree = new HashMap<>();
        Map<String, List<String>> successors = new LinkedHashMap<>();
        for (String id : nodeIds) {
            inDegree.put(id, 0);
            successors.put(id, new ArrayList<>());
        }
        for (EdgeDefinition edge : edges) {
            successors.get(edge.from()).add(edge.to());
            inDegree.merge(edge.to(), 1, Integer::sum);
        }
        Deque<String> ready = new ArrayDeque<>();
        inDegree.forEach((id, degree) -> {
            if (degree == 0) {
                ready.add(id);
            }
        });
        while (!ready.isEmpty()) {
            String id = ready.poll();
            inDegree.remove(id);
            for (String next : successors.get(id)) {
                if (inDegree.merge(next, -1, Integer::sum) == 0) {
                    ready.add(next);
                }
            }
        }
        return new TreeSet<>(inDegree.keySet());
    }
}
