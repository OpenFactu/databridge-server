package com.openfactu.databridge.infrastructure.secret;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.openfactu.databridge.core.connection.SecretRef;
import com.openfactu.databridge.core.error.ConfigurationException;
import java.util.Map;
import org.junit.jupiter.api.Test;

class EnvironmentSecretResolverTest {

    private final EnvironmentSecretResolver resolver =
            new EnvironmentSecretResolver(Map.of("PG_PASSWORD", "s3cr3t", "EMPTY", "")::get);

    @Test
    void resolvesDefinedVariable() {
        assertThat(resolver.resolve(SecretRef.env("PG_PASSWORD"))).isEqualTo("s3cr3t");
    }

    @Test
    void failsWithActionableMessageForMissingOrEmptyVariable() {
        assertThatThrownBy(() -> resolver.resolve(SecretRef.env("MISSING")))
                .isInstanceOf(ConfigurationException.class)
                .hasMessageContaining("'MISSING'");
        assertThatThrownBy(() -> resolver.resolve(SecretRef.env("EMPTY"))).isInstanceOf(ConfigurationException.class);
    }
}
