package com.jimdimas.api.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/*helper class for fetching the products and their amount from a POST request at order endpoint,
because a nested Product cannot be initialized automatically by @RequestBody */
public class RequestSingleProduct {

    private UUID productId;
    private Integer amount;
}
