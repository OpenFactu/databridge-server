package com.openfactu.databridge.core.workflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.openfactu.databridge.core.error.ValidationException;
import com.openfactu.databridge.core.validation.ValidationIssue;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class WorkflowValidatorTest {

    private final WorkflowValidator validator = new WorkflowValidator(Set.of("read", "map", "write"));

    @Test
    void acceptsLinearWorkflow() {
        var workflow = workflow(
                List.of(node("a", "read"), node("b", "map"), node("c", "write")),
                List.of(edge("a", "b"), edge("b", "c")));

        assertThat(validator.validate(workflow)).isEmpty();
    }

    @Test
    void rejectsCycles() {
        var workflow = workflow(
                List.of(node("a", "read"), node("b", "map"), node("c", "map")),
                List.of(edge("a", "b"), edge("b", "c"), edge("c", "b")));

        assertThat(validator.validate(workflow))
                .singleElement()
                .satisfies(
                        issue -> assertThat(issue.message()).contains("ciclos").contains("[b, c]"));
    }

    @Test
    void rejectsSelfLoop() {
        var workflow = workflow(List.of(node("a", "map")), List.of(edge("a", "a")));

        assertThat(validator.validate(workflow))
                .extracting(ValidationIssue::path)
                .containsExactly("edges");
    }

    @Test
    void rejectsUnknownNodeType() {
        var workflow = workflow(List.of(node("a", "script")), List.of());

        assertThat(validator.validate(workflow)).singleElement().satisfies(issue -> {
            assertThat(issue.path()).isEqualTo("nodes[0].type");
            assertThat(issue.message()).contains("'script'").contains("[map, read, write]");
        });
    }

    @Test
    void rejectsDanglingAndDuplicateEdges() {
        var workflow = workflow(
                List.of(node("a", "read"), node("b", "write")),
                List.of(edge("a", "b"), edge("a", "b"), edge("a", "zzz")));

        assertThat(validator.validate(workflow))
                .extracting(ValidationIssue::path)
                .containsExactly("edges[1]", "edges[2].to");
    }

    @Test
    void reportsAllProblemsAtOnce() {
        var workflow =
                new WorkflowDefinition(2, "bad id", 0, null, List.of(node("a", "read"), node("a", "read")), List.of());

        assertThatThrownBy(() -> validator.validateOrThrow(workflow))
                .isInstanceOfSatisfying(
                        ValidationException.class,
                        e -> assertThat(e.issues())
                                .extracting(ValidationIssue::path)
                                .containsExactly("formatVersion", "id", "version", "nodes[1].id"));
    }

    @Test
    void requiresAtLeastOneNode() {
        assertThat(validator.validate(workflow(List.of(), List.of())))
                .extracting(ValidationIssue::path)
                .containsExactly("nodes");
    }

    private static WorkflowDefinition workflow(List<NodeDefinition> nodes, List<EdgeDefinition> edges) {
        return new WorkflowDefinition(WorkflowDefinition.CURRENT_FORMAT_VERSION, "test", 1, null, nodes, edges);
    }

    private static NodeDefinition node(String id, String type) {
        return new NodeDefinition(id, type, Map.of());
    }

    private static EdgeDefinition edge(String from, String to) {
        return new EdgeDefinition(from, to);
    }
}
