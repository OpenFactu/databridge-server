package com.openfactu.databridge.core.connection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.openfactu.databridge.core.error.ConfigurationException;
import org.junit.jupiter.api.Test;

class SecretRefTest {

    @Test
    void parsesEnvironmentReference() {
        SecretRef ref = SecretRef.parse("env:PG_PASSWORD");

        assertThat(ref.provider()).isEqualTo(SecretRef.Provider.ENV);
        assertThat(ref.key()).isEqualTo("PG_PASSWORD");
        assertThat(ref).hasToString("env:PG_PASSWORD");
    }

    @Test
    void rejectsLiteralSecret() {
        assertThatThrownBy(() -> SecretRef.parse("s3cr3t!"))
                .isInstanceOf(ConfigurationException.class)
                .hasMessageContaining("env:NOMBRE_VARIABLE")
                .hasMessageNotContaining("s3cr3t!");
    }

    @Test
    void rejectsInvalidVariableName() {
        assertThatThrownBy(() -> SecretRef.parse("env:not a var")).isInstanceOf(ConfigurationException.class);
    }
}
