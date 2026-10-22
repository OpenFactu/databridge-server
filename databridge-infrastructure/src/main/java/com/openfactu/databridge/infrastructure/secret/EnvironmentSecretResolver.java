package com.openfactu.databridge.infrastructure.secret;

import com.openfactu.databridge.core.connection.SecretRef;
import com.openfactu.databridge.core.connection.SecretResolver;
import com.openfactu.databridge.core.error.ConfigurationException;
import java.util.function.UnaryOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Resuelve secretos {@code env:NOMBRE} desde variables de entorno. Nunca registra el valor. */
public final class EnvironmentSecretResolver implements SecretResolver {

    private static final Logger log = LoggerFactory.getLogger(EnvironmentSecretResolver.class);

    private final UnaryOperator<String> environment;

    public EnvironmentSecretResolver() {
        this(System::getenv);
    }

    /** @param environment fuente de variables; permite sustituir el entorno real en tests */
    public EnvironmentSecretResolver(UnaryOperator<String> environment) {
        this.environment = environment;
    }

    @Override
    public String resolve(SecretRef ref) {
        if (ref.provider() != SecretRef.Provider.ENV) {
            throw new ConfigurationException("Proveedor de secretos no soportado: " + ref.provider());
        }
        String value = environment.apply(ref.key());
        if (value == null || value.isEmpty()) {
            throw new ConfigurationException("La variable de entorno '" + ref.key()
                    + "' referenciada como secreto no está definida o está vacía.");
        }
        log.debug("Secreto resuelto desde la variable de entorno {}", ref.key());
        return value;
    }
}
