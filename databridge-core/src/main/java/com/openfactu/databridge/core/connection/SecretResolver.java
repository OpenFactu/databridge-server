package com.openfactu.databridge.core.connection;

/** Resuelve referencias de secretos en el momento de abrir una conexión. */
@FunctionalInterface
public interface SecretResolver {

    /**
     * Devuelve el valor del secreto. Las implementaciones no deben registrar el valor en logs ni
     * incluirlo en mensajes de error.
     *
     * @throws com.openfactu.databridge.core.error.ConfigurationException si no se puede resolver
     */
    String resolve(SecretRef ref);
}
