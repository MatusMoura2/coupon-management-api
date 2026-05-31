package com.couponmanagementapi.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SpringDataCouponRepository extends JpaRepository<CouponJpaEntity, UUID> {
}
