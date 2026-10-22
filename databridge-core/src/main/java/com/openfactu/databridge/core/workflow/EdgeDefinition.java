package com.openfactu.databridge.core.workflow;

/** Arista dirigida: los registros que produce {@code from} llegan a {@code to}. */
public record EdgeDefinition(String from, String to) {}
