package com.couponmanagementapi.infrastructure.configuration;

import com.couponmanagementapi.application.usecase.CreateCouponUseCase;
import com.couponmanagementapi.application.usecase.DeleteCouponUseCase;
import com.couponmanagementapi.application.usecase.GetCouponUseCase;
import com.couponmanagementapi.domain.repository.CouponRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public CreateCouponUseCase createCouponUseCase(CouponRepository repository) {
        return new CreateCouponUseCase(repository);
    }

    @Bean
    public GetCouponUseCase getCouponUseCase(CouponRepository repository) {
        return new GetCouponUseCase(repository);
    }

    @Bean
    public DeleteCouponUseCase deleteCouponUseCase(CouponRepository repository) {
        return new DeleteCouponUseCase(repository);
    }
}
