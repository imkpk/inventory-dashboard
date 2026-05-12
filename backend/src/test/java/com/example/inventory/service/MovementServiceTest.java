package com.example.inventory.service;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.inventory.model.MovementType;
import com.example.inventory.model.StockMovement;
import com.fasterxml.jackson.databind.ObjectMapper;

class MovementServiceTest {
  private MovementService service;

  @BeforeEach
  void setUp() {
    service = new MovementService(new ObjectMapper());
  }

  @Test
  void shouldFilterMovementsByDateRangeAndTypeInDescendingOrder() throws Exception {
    setMovements(List.of(
        new StockMovement("mv1", Instant.parse("2026-03-10T10:00:00Z"), "SKU1", MovementType.IN, 5),
        new StockMovement("mv2", Instant.parse("2026-03-12T12:00:00Z"), "SKU2", MovementType.OUT, 3),
        new StockMovement("mv3", Instant.parse("2026-03-11T08:00:00Z"), "SKU3", MovementType.IN, 7)));

    List<StockMovement> results = service.findMovements(
        LocalDate.parse("2026-03-10"),
        LocalDate.parse("2026-03-12"),
        MovementType.IN);

    assertThat(results).hasSize(2);
    assertThat(results.get(0).getId()).isEqualTo("mv3");
    assertThat(results.get(1).getId()).isEqualTo("mv1");
  }

  @Test
  void shouldThrowWhenFromDateIsNull() {
    assertThatThrownBy(() -> service.findMovements(null, LocalDate.parse("2026-03-12"), null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("from and to query params are required");
  }

  @Test
  void shouldThrowWhenFromDateAfterToDate() {
    assertThatThrownBy(() -> service.findMovements(
        LocalDate.parse("2026-03-13"),
        LocalDate.parse("2026-03-12"),
        null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("from date cannot be after to date");
  }

  @SuppressWarnings("unchecked")
  private void setMovements(List<StockMovement> movements) throws Exception {
    Field field = MovementService.class.getDeclaredField("movements");
    field.setAccessible(true);
    List<StockMovement> target = (List<StockMovement>) field.get(service);
    target.clear();
    target.addAll(movements);
  }
}
