package com.couponmanagementapi.domain.model;

import com.couponmanagementapi.domain.exception.DomainException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CouponTest {

    @Test
    void shouldCreateCouponAndCleanCodeSuccessfully() {
        Coupon coupon = Coupon.create("A-B@C#1$2_3", "10% off", new BigDecimal("10.0"), LocalDateTime.now().plusDays(1), true);

        assertEquals("ABC123", coupon.getCode());
        assertEquals("10% off", coupon.getDescription());
        assertEquals(new BigDecimal("10.0"), coupon.getDiscountValue());
        assertEquals(CouponStatus.ACTIVE, coupon.getStatus());
        assertTrue(coupon.isPublished());
        assertFalse(coupon.isRedeemed());
        assertNotNull(coupon.getId());
    }

    @Test
    void shouldThrowExceptionWhenCodeIsInvalidAfterCleaning() {
        DomainException ex = assertThrows(DomainException.class, () ->
                Coupon.create("AB-12", "Invalid code", new BigDecimal("5.0"), LocalDateTime.now().plusDays(1), true)
        );
        assertTrue(ex.getMessage().contains("exactly 6 alphanumeric characters"));
    }

    @Test
    void shouldThrowExceptionWhenDiscountValueIsLessThanHalf() {
        DomainException ex = assertThrows(DomainException.class, () ->
                Coupon.create("ABC123", "Too low", new BigDecimal("0.49"), LocalDateTime.now().plusDays(1), true)
        );
        assertTrue(ex.getMessage().contains("must be at least 0.5"));
    }

    @Test
    void shouldThrowExceptionWhenExpirationDateIsInThePast() {
        DomainException ex = assertThrows(DomainException.class, () ->
                Coupon.create("ABC123", "Expired", new BigDecimal("5.0"), LocalDateTime.now().minusMinutes(5), true)
        );
        assertTrue(ex.getMessage().contains("cannot be in the past"));
    }

    @Test
    void shouldSoftDeleteCouponSuccessfully() {
        Coupon coupon = Coupon.create("ABC123", "Valid", new BigDecimal("5.0"), LocalDateTime.now().plusDays(1), true);
        assertEquals(CouponStatus.ACTIVE, coupon.getStatus());

        coupon.softDelete();
        assertEquals(CouponStatus.DELETED, coupon.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenDeletingAlreadyDeletedCoupon() {
        Coupon coupon = Coupon.create("ABC123", "Valid", new BigDecimal("5.0"), LocalDateTime.now().plusDays(1), true);
        coupon.softDelete();

        DomainException ex = assertThrows(DomainException.class, coupon::softDelete);
        assertTrue(ex.getMessage().contains("already deleted"));
    }
}
