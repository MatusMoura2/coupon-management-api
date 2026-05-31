package com.couponmanagementapi.application.usecase;

import com.couponmanagementapi.domain.exception.ResourceNotFoundException;
import com.couponmanagementapi.domain.model.Coupon;
import com.couponmanagementapi.domain.repository.CouponRepository;

import java.util.UUID;

public class DeleteCouponUseCase {
    private final CouponRepository repository;

    public DeleteCouponUseCase(CouponRepository repository) {
        this.repository = repository;
    }

    public void execute(UUID id) {
        Coupon coupon = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + id));
        
        coupon.softDelete();
        repository.save(coupon);
    }
}
