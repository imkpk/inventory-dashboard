package com.example.inventory.service;

import com.example.inventory.model.StockMovement;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CsvExportService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public String toCsv(List<StockMovement> movements) {
        StringBuilder csv = new StringBuilder("id,timestamp,sku,movementType,quantity\n");

        for (StockMovement movement : movements) {
            csv.append(escape(movement.getId())).append(',')
                    .append(FORMATTER.format(movement.getTimestamp().atOffset(ZoneOffset.UTC))).append(',')
                    .append(escape(movement.getSku())).append(',')
                    .append(movement.getMovementType()).append(',')
                    .append(movement.getQuantity())
                    .append('\n');
        }

        return csv.toString();
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }

        boolean needsQuotes = value.contains(",") || value.contains("\"") || value.contains("\n");

        String escaped = value.replace("\"", "\"\"");

        return needsQuotes ? "\"" + escaped + "\"" : escaped;
    }
}