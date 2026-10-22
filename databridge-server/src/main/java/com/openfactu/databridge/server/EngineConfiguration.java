package com.openfactu.databridge.server;

import com.openfactu.databridge.core.connection.SecretResolver;
import com.openfactu.databridge.core.json.DefinitionJsonReader;
import com.openfactu.databridge.engine.NodeTypeRegistry;
import com.openfactu.databridge.engine.WorkflowLoader;
import com.openfactu.databridge.infrastructure.secret.EnvironmentSecretResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Conecta los módulos sin Spring (core, engine, infrastructure) mediante beans explícitos. */
@Configuration(proxyBeanMethods = false)
class EngineConfiguration {

    @Bean
    DefinitionJsonReader definitionJsonReader() {
        return new DefinitionJsonReader();
    }

    @Bean
    NodeTypeRegistry nodeTypeRegistry() {
        return new NodeTypeRegistry();
    }

    @Bean
    WorkflowLoader workflowLoader(DefinitionJsonReader reader, NodeTypeRegistry registry) {
        return new WorkflowLoader(reader, registry);
    }

    @Bean
    SecretResolver secretResolver() {
        return new EnvironmentSecretResolver();
    }
}
