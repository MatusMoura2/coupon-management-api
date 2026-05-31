package com.couponmanagementapi.application.usecase;

import com.couponmanagementapi.domain.model.Coupon;
import com.couponmanagementapi.domain.repository.CouponRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreateCouponUseCase {
    private final CouponRepository repository;

    public CreateCouponUseCase(CouponRepository repository) {
        this.repository = repository;
    }

    public Coupon execute(String rawCode, String description, BigDecimal discountValue, LocalDateTime expirationDate, boolean published) {
        Coupon coupon = Coupon.create(rawCode, description, discountValue, expirationDate, published);
        return repository.save(coupon);
    }
}
