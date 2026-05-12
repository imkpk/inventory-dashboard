package com.example.inventory.service;

import com.example.inventory.model.MovementType;
import com.example.inventory.model.StockMovement;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CsvExportServiceTest {
    @Test
    void shouldExportCsvWithHeaderAndRows() {
        CsvExportService service = new CsvExportService();

        String csv = service.toCsv(List.of(
                new StockMovement("mv1001", Instant.parse("2026-03-10T17:46:00Z"), "SKU003", MovementType.IN, 48)
        ));

        assertThat(csv).contains("id,timestamp,sku,movementType,quantity");
        assertThat(csv).contains("mv1001,2026-03-10T17:46:00Z,SKU003,IN,48");
    }
}
