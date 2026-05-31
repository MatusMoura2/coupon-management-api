package com.couponmanagementapi.application.usecase;

import com.couponmanagementapi.domain.exception.ResourceNotFoundException;
import com.couponmanagementapi.domain.model.Coupon;
import com.couponmanagementapi.domain.model.CouponStatus;
import com.couponmanagementapi.domain.repository.CouponRepository;

import java.util.UUID;

public class GetCouponUseCase {
    private final CouponRepository repository;

    public GetCouponUseCase(CouponRepository repository) {
        this.repository = repository;
    }

    public Coupon execute(UUID id) {
        Coupon coupon = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + id));
        
        if (coupon.getStatus() == CouponStatus.DELETED) {
            throw new ResourceNotFoundException("Coupon not found with id: " + id);
        }
        
        return coupon;
    }
}
