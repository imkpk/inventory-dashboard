package com.example.inventory.service;

import com.example.inventory.model.MovementType;
import com.example.inventory.model.StockMovement;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class MovementService {
    private final ObjectMapper objectMapper;
    private final List<StockMovement> movements = new ArrayList<>();

    public MovementService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void loadData() throws Exception {
        ClassPathResource resource = new ClassPathResource("mock_movements.json");
        try (InputStream inputStream = resource.getInputStream()) {
            List<StockMovement> loaded = objectMapper.readValue(inputStream, new TypeReference<>() {});
            movements.clear();
            movements.addAll(loaded);
        }
    }

    public List<StockMovement> findMovements(LocalDate from, LocalDate to, MovementType type) {
        validateDateRange(from, to);

        return movements.stream()
                .filter(movement -> {
                    LocalDate movementDate = movement.getTimestamp()
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate();

                    boolean isInsideDateRange = !movementDate.isBefore(from) && !movementDate.isAfter(to);
                    boolean matchesType = type == null || movement.getMovementType() == type;

                    return isInsideDateRange && matchesType;
                })
                .sorted(Comparator.comparing(StockMovement::getTimestamp).reversed())
                .toList();
    }

    private void validateDateRange(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("from and to query params are required");
        }

        if (from.isAfter(to)) {
            throw new IllegalArgumentException("from date cannot be after to date");
        }
    }
}
