package com.jimdimas.api.product;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.UUID;

public interface ProductProjection {

    UUID getProductId();
    String getName();
    String getDescription();
    Category getCategory();
    Integer getPrice();
    LocalDate getCreationDate();
    Integer getQuantity();
}
