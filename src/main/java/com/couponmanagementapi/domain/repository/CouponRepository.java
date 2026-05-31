package com.couponmanagementapi.domain.repository;

import com.couponmanagementapi.domain.model.Coupon;

import java.util.Optional;
import java.util.UUID;

public interface CouponRepository {
    Coupon save(Coupon coupon);
    Optional<Coupon> findById(UUID id);
}
