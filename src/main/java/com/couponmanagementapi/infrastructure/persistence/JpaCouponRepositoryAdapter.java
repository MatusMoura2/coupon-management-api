package com.couponmanagementapi.infrastructure.persistence;

import com.couponmanagementapi.domain.model.Coupon;
import com.couponmanagementapi.domain.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JpaCouponRepositoryAdapter implements CouponRepository {

    private final SpringDataCouponRepository springDataRepository;

    @Override
    public Coupon save(Coupon coupon) {
        CouponJpaEntity entity = CouponJpaEntity.fromDomain(coupon);
        CouponJpaEntity saved = springDataRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Coupon> findById(UUID id) {
        return springDataRepository.findById(id)
                .map(CouponJpaEntity::toDomain);
    }
}
