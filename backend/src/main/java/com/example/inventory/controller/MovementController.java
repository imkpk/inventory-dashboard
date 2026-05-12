package com.example.inventory.controller;

import com.example.inventory.model.MovementType;
import com.example.inventory.model.StockMovement;
import com.example.inventory.service.CsvExportService;
import com.example.inventory.service.MovementService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/movements")
@CrossOrigin(origins = "http://localhost:5173")
public class MovementController {
    private final MovementService movementService;
    private final CsvExportService csvExportService;

    public MovementController(MovementService movementService, CsvExportService csvExportService) {
        this.movementService = movementService;
        this.csvExportService = csvExportService;
    }

    @GetMapping
    public ResponseEntity<?> getMovements(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) MovementType type,
            @RequestParam(defaultValue = "false") boolean export
    ) {
        List<StockMovement> filteredMovements = movementService.findMovements(from, to, type);

        if (export) {
            String csv = csvExportService.toCsv(filteredMovements);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.attachment()
                                    .filename("stock-movements.csv")
                                    .build()
                                    .toString())
                    .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                    .body(csv);
        }

        return ResponseEntity.ok(filteredMovements);
    }
}
