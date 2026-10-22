package com.openfactu.databridge.engine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.openfactu.databridge.core.error.ValidationException;
import com.openfactu.databridge.core.json.DefinitionJsonReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class WorkflowLoaderTest {

    private static final String WORKFLOW = """
            {"formatVersion": 1, "id": "copy", "version": 3,
             "nodes": [{"id": "src", "type": "read"}, {"id": "dst", "type": "write"}],
             "edges": [{"from": "src", "to": "dst"}]}
            """;

    @TempDir
    Path dir;

    @Test
    void loadsWorkflowWithRegisteredTypes() throws IOException {
        var registry = new NodeTypeRegistry().register("read").register("write");
        var loader = new WorkflowLoader(new DefinitionJsonReader(), registry);

        var workflow = loader.load(Files.writeString(dir.resolve("copy.json"), WORKFLOW));

        assertThat(workflow.version()).isEqualTo(3);
    }

    @Test
    void rejectsTypesNotRegisteredInEngine() throws IOException {
        var loader = new WorkflowLoader(new DefinitionJsonReader(), new NodeTypeRegistry().register("read"));
        Path file = Files.writeString(dir.resolve("copy.json"), WORKFLOW);

        assertThatThrownBy(() -> loader.load(file))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("nodes[1].type");
    }

    @Test
    void rejectsDuplicateRegistration() {
        var registry = new NodeTypeRegistry().register("read");

        assertThatThrownBy(() -> registry.register("read")).isInstanceOf(IllegalArgumentException.class);
    }
}
