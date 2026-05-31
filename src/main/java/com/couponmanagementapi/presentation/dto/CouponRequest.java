package com.couponmanagementapi.presentation.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CouponRequest {

    @NotBlank(message = "Code is required")
    private String code;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.5", message = "Discount value must be at least 0.5")
    private BigDecimal discountValue;

    @NotNull(message = "Expiration date is required")
    private LocalDateTime expirationDate;

    private Boolean published;
}
