package com.couponmanagementapi.domain.model;

import com.couponmanagementapi.domain.exception.DomainException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Coupon {
    private final UUID id;
    private final String code;
    private final String description;
    private final BigDecimal discountValue;
    private final LocalDateTime expirationDate;
    private CouponStatus status;
    private final boolean published;
    private final boolean redeemed;

    // Factory method for creating a brand new Coupon
    public static Coupon create(String rawCode, String description, BigDecimal discountValue, LocalDateTime expirationDate, boolean published) {
        validateDescription(description);
        validateDiscountValue(discountValue);
        validateExpirationDateForCreation(expirationDate);
        String cleanedCode = cleanAndValidateCode(rawCode);

        return new Coupon(
            UUID.randomUUID(),
            cleanedCode,
            description,
            discountValue,
            expirationDate,
            CouponStatus.ACTIVE,
            published,
            false
        );
    }

    // Factory method for reconstituting Coupon from database (ignores "expiration date in the past" rule since it is already saved)
    public static Coupon reconstitute(UUID id, String code, String description, BigDecimal discountValue, LocalDateTime expirationDate, CouponStatus status, boolean published, boolean redeemed) {
        return new Coupon(id, code, description, discountValue, expirationDate, status, published, redeemed);
    }

    // Private constructor
    private Coupon(UUID id, String code, String description, BigDecimal discountValue, LocalDateTime expirationDate, CouponStatus status, boolean published, boolean redeemed) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.discountValue = discountValue;
        this.expirationDate = expirationDate;
        this.status = status;
        this.published = published;
        this.redeemed = redeemed;
    }

    // Business behaviors
    public void softDelete() {
        if (this.status == CouponStatus.DELETED) {
            throw new DomainException("Coupon is already deleted");
        }
        this.status = CouponStatus.DELETED;
    }

    // Domain validation methods
    private static void validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new DomainException("Description is required");
        }
    }

    private static String cleanAndValidateCode(String rawCode) {
        if (rawCode == null || rawCode.trim().isEmpty()) {
            throw new DomainException("Code is required");
        }
        String cleaned = rawCode.replaceAll("[^a-zA-Z0-9]", "");
        if (cleaned.length() != 6) {
            throw new DomainException("Code must have exactly 6 alphanumeric characters after removing special characters");
        }
        return cleaned;
    }

    private static void validateDiscountValue(BigDecimal discountValue) {
        if (discountValue == null) {
            throw new DomainException("Discount value is required");
        }
        if (discountValue.compareTo(new BigDecimal("0.5")) < 0) {
            throw new DomainException("Discount value must be at least 0.5");
        }
    }

    private static void validateExpirationDateForCreation(LocalDateTime expirationDate) {
        if (expirationDate == null) {
            throw new DomainException("Expiration date is required");
        }
        if (expirationDate.isBefore(LocalDateTime.now())) {
            throw new DomainException("Expiration date cannot be in the past");
        }
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public CouponStatus getStatus() {
        return status;
    }

    public boolean isPublished() {
        return published;
    }

    public boolean isRedeemed() {
        return redeemed;
    }
}
