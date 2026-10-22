package com.openfactu.databridge.server;

import static org.assertj.core.api.Assertions.assertThat;

import com.openfactu.databridge.engine.WorkflowLoader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DataBridgeServerApplicationTest {

    @Autowired
    WorkflowLoader workflowLoader;

    @Test
    void contextLoadsAndWiresEngine() {
        assertThat(workflowLoader).isNotNull();
    }
}
