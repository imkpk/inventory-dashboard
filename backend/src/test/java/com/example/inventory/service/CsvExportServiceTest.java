package com.example.inventory.service;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import com.example.inventory.model.MovementType;
import com.example.inventory.model.StockMovement;

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

    @Test
    void shouldEscapeFieldsContainingCommasAndQuotes() {
        CsvExportService service = new CsvExportService();

        String csv = service.toCsv(List.of(
                new StockMovement("mv1002", Instant.parse("2026-03-10T17:46:00Z"), "SKU,003\"X\"", MovementType.OUT, 12)
        ));

        assertThat(csv).contains("\"SKU,003\"\"X\"\"\"");
        assertThat(csv).contains("mv1002,2026-03-10T17:46:00Z,\"SKU,003\"\"X\"\"\",OUT,12");
    }

    @Test
    void shouldHandleNullValuesForIdAndSku() {
        CsvExportService service = new CsvExportService();

        String csv = service.toCsv(List.of(
                new StockMovement(null, Instant.parse("2026-03-10T17:46:00Z"), null, MovementType.IN, 10)
        ));

        assertThat(csv).contains(",2026-03-10T17:46:00Z,,IN,10");
    }
}
