package com.example.inventory.controller;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.example.inventory.model.MovementType;
import com.example.inventory.model.StockMovement;
import com.example.inventory.service.CsvExportService;
import com.example.inventory.service.MovementService;

@ExtendWith(MockitoExtension.class)
class MovementControllerTest {
  @Mock
  private MovementService movementService;

  @Mock
  private CsvExportService csvExportService;

  @InjectMocks
  private MovementController controller;

  @Test
  void shouldReturnFilteredMovementsWhenExportIsFalse() {
    List<StockMovement> expected = List.of(
        new StockMovement("mv1", Instant.parse("2026-03-10T10:00:00Z"), "SKU1", MovementType.IN, 5));

    when(movementService.findMovements(
        eq(LocalDate.parse("2026-03-10")),
        eq(LocalDate.parse("2026-03-11")),
        eq(MovementType.IN))).thenReturn(expected);

    ResponseEntity<?> response = controller.getMovements(
        LocalDate.parse("2026-03-10"),
        LocalDate.parse("2026-03-11"),
        MovementType.IN,
        false);

    assertThat(response.getStatusCodeValue()).isEqualTo(200);
    assertThat(response.getBody()).isEqualTo(expected);
  }

  @Test
  void shouldReturnCsvAttachmentWhenExportIsTrue() {
    List<StockMovement> expected = List.of(
        new StockMovement("mv2", Instant.parse("2026-03-12T12:00:00Z"), "SKU2", MovementType.OUT, 3));

    when(movementService.findMovements(
        eq(LocalDate.parse("2026-03-10")),
        eq(LocalDate.parse("2026-03-12")),
        eq(null))).thenReturn(expected);
    when(csvExportService.toCsv(expected)).thenReturn("csv-content");

    ResponseEntity<?> response = controller.getMovements(
        LocalDate.parse("2026-03-10"),
        LocalDate.parse("2026-03-12"),
        null,
        true);

    assertThat(response.getStatusCodeValue()).isEqualTo(200);
    assertThat(response.getHeaders().getFirst("Content-Disposition")).contains("stock-movements.csv");
    assertThat(response.getHeaders().getContentType().toString()).isEqualTo("text/csv;charset=UTF-8");
    assertThat(response.getBody()).isEqualTo("csv-content");
  }
}
