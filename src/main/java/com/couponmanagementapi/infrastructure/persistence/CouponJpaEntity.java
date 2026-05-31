package com.couponmanagementapi.infrastructure.persistence;

import com.couponmanagementapi.domain.model.Coupon;
import com.couponmanagementapi.domain.model.CouponStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
public class CouponJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 6)
    private String code;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "discount_value", nullable = false, precision = 19, scale = 4)
    private BigDecimal discountValue;

    @Column(name = "expiration_date", nullable = false)
    private LocalDateTime expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponStatus status;

    @Column(nullable = false)
    private boolean published;

    @Column(nullable = false)
    private boolean redeemed;

    public static CouponJpaEntity fromDomain(Coupon coupon) {
        CouponJpaEntity entity = new CouponJpaEntity();
        entity.setId(coupon.getId());
        entity.setCode(coupon.getCode());
        entity.setDescription(coupon.getDescription());
        entity.setDiscountValue(coupon.getDiscountValue());
        entity.setExpirationDate(coupon.getExpirationDate());
        entity.setStatus(coupon.getStatus());
        entity.setPublished(coupon.isPublished());
        entity.setRedeemed(coupon.isRedeemed());
        return entity;
    }

    public Coupon toDomain() {
        return Coupon.reconstitute(
                this.id,
                this.code,
                this.description,
                this.discountValue,
                this.expirationDate,
                this.status,
                this.published,
                this.redeemed
        );
    }
}
