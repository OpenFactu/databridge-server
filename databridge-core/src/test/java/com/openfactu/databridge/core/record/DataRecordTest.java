package com.openfactu.databridge.core.record;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class DataRecordTest {

    @Test
    void keepsFieldOrderAndAllowsNulls() {
        DataRecord record = DataRecord.builder()
                .set("id", 7L)
                .set("email", null)
                .set("name", "Ana")
                .build();

        assertThat(record.fieldNames()).containsExactly("id", "email", "name");
        assertThat(record.get("email")).isNull();
        assertThat(record.get("id", Long.class)).isEqualTo(7L);
    }

    @Test
    void isImmutable() {
        Map<String, Object> source = new HashMap<>(Map.of("id", 1L));
        DataRecord record = DataRecord.of(source);
        source.put("id", 2L);

        assertThat(record.get("id")).isEqualTo(1L);
        assertThatThrownBy(() -> record.asMap().put("x", 1)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void failsOnMissingFieldOrWrongType() {
        DataRecord record = DataRecord.of(Map.of("id", 1L));

        assertThatThrownBy(() -> record.get("missing")).hasMessageContaining("'missing'");
        assertThatThrownBy(() -> record.get("id", String.class)).hasMessageContaining("Long");
    }

    @Test
    void toStringDoesNotExposeValues() {
        assertThat(DataRecord.of(Map.of("email", "ana@example.com")).toString()).doesNotContain("ana@");
    }
}
