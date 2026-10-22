package com.openfactu.databridge.core.connection;

import com.openfactu.databridge.core.error.ConfigurationException;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Referencia a un secreto guardado fuera de las definiciones (nunca el secreto en sí).
 *
 * <p>Formato textual {@code proveedor:clave}. De momento solo existe el proveedor {@code env}
 * (variable de entorno); un almacén seguro se añadirá más adelante como otro proveedor.
 */
public record SecretRef(Provider provider, String key) {

    /** Origen del que se resuelve el secreto. */
    public enum Provider {
        ENV
    }

    private static final Pattern ENV_KEY = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");

    public SecretRef {
        Objects.requireNonNull(provider, "provider");
        if (key == null || !ENV_KEY.matcher(key).matches()) {
            throw new ConfigurationException("Clave de secreto inválida '" + key
                    + "': debe ser un nombre de variable de entorno (letras, dígitos y '_').");
        }
    }

    public static SecretRef env(String variable) {
        return new SecretRef(Provider.ENV, variable);
    }

    /**
     * Interpreta una referencia {@code env:NOMBRE}. Rechaza cualquier otro texto para que un secreto
     * literal escrito por error en una definición no se acepte silenciosamente.
     */
    public static SecretRef parse(String text) {
        if (text == null || !text.startsWith("env:")) {
            throw new ConfigurationException("Referencia de secreto inválida: se esperaba 'env:NOMBRE_VARIABLE'."
                    + " Los secretos no se escriben en las definiciones, solo se referencian.");
        }
        return env(text.substring("env:".length()));
    }

    @Override
    public String toString() {
        return provider.name().toLowerCase(java.util.Locale.ROOT) + ":" + key;
    }
}
