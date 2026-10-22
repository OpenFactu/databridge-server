package com.openfactu.databridge.core.schema;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;

class DataSchemaTest {

    @Test
    void exposesFieldsAndKeys() {
        var schema = new DataSchema(
                "customers",
                List.of(
                        new FieldSchema("id", LogicalType.LONG, false, true),
                        new FieldSchema("email", LogicalType.STRING, true, false)));

        assertThat(schema.field("email")).map(FieldSchema::type).contains(LogicalType.STRING);
        assertThat(schema.field("missing")).isEmpty();
        assertThat(schema.keyFields()).extracting(FieldSchema::name).containsExactly("id");
    }

    @Test
    void rejectsDuplicateFieldNames() {
        var field = new FieldSchema("id", LogicalType.LONG, false, true);

        assertThatThrownBy(() -> new DataSchema("customers", List.of(field, field)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("'id'");
    }
}
