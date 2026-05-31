package com.couponmanagementapi.presentation.dto;

import com.couponmanagementapi.domain.model.Coupon;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CouponResponse {
    private UUID id;
    private String code;
    private String description;
    private BigDecimal discountValue;
    private LocalDateTime expirationDate;
    private String status;
    private boolean published;
    private boolean redeemed;

    public static CouponResponse fromDomain(Coupon coupon) {
        return CouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .description(coupon.getDescription())
                .discountValue(coupon.getDiscountValue())
                .expirationDate(coupon.getExpirationDate())
                .status(coupon.getStatus().name())
                .published(coupon.isPublished())
                .redeemed(coupon.isRedeemed())
                .build();
    }
}
