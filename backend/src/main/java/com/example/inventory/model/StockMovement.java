package com.example.inventory.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;

public class StockMovement {
    private String id;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant timestamp;

    private String sku;
    private MovementType movementType;
    private int quantity;

    public StockMovement() {
    }

    public StockMovement(String id, Instant timestamp, String sku, MovementType movementType, int quantity) {
        this.id = id;
        this.timestamp = timestamp;
        this.sku = sku;
        this.movementType = movementType;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getSku() {
        return sku;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
