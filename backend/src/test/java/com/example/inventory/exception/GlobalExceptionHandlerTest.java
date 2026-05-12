package com.example.inventory.exception;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

class GlobalExceptionHandlerTest {
  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  void shouldReturnBadRequestForIllegalArgumentException() {
    ResponseEntity<Map<String, Object>> response = handler
        .handleBadRequest(new IllegalArgumentException("invalid request"));

    assertThat(response.getStatusCodeValue()).isEqualTo(400);
    assertThat(response.getBody()).containsEntry("status", 400);
    assertThat(response.getBody()).containsEntry("error", "Bad Request");
    assertThat(response.getBody()).containsEntry("message", "invalid request");
  }

  @Test
  void shouldReturnBadRequestForTypeMismatch() {
    MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException("abc", String.class, "from",
        null, new IllegalArgumentException());

    ResponseEntity<Map<String, Object>> response = handler.handleTypeMismatch(exception);

    assertThat(response.getStatusCodeValue()).isEqualTo(400);
    assertThat(response.getBody()).containsEntry("status", 400);
    assertThat(response.getBody()).containsEntry("error", "Bad Request");
    assertThat(response.getBody().get("message")).asString().contains("Invalid query parameter value: from");
  }
}
