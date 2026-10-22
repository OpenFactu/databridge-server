package com.openfactu.databridge.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del backend. Por ahora solo ensambla los módulos; la API de administración llegará
 * en fases posteriores.
 */
@SpringBootApplication
public class DataBridgeServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataBridgeServerApplication.class, args);
    }
}
