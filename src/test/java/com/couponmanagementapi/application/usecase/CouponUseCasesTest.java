package com.couponmanagementapi.application.usecase;

import com.couponmanagementapi.domain.exception.DomainException;
import com.couponmanagementapi.domain.exception.ResourceNotFoundException;
import com.couponmanagementapi.domain.model.Coupon;
import com.couponmanagementapi.domain.model.CouponStatus;
import com.couponmanagementapi.domain.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponUseCasesTest {

    @Mock
    private CouponRepository repository;

    private CreateCouponUseCase createUseCase;
    private GetCouponUseCase getUseCase;
    private DeleteCouponUseCase deleteUseCase;

    @BeforeEach
    void setUp() {
        createUseCase = new CreateCouponUseCase(repository);
        getUseCase = new GetCouponUseCase(repository);
        deleteUseCase = new DeleteCouponUseCase(repository);
    }

    @Test
    void shouldCreateCouponSuccessfully() {
        String rawCode = "ABC-123";
        String description = "Test discount";
        BigDecimal discountValue = new BigDecimal("15.50");
        LocalDateTime expiration = LocalDateTime.now().plusDays(2);
        boolean published = true;

        when(repository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Coupon result = createUseCase.execute(rawCode, description, discountValue, expiration, published);

        assertNotNull(result);
        assertEquals("ABC123", result.getCode());
        assertEquals(description, result.getDescription());
        assertEquals(discountValue, result.getDiscountValue());
        assertEquals(CouponStatus.ACTIVE, result.getStatus());
        assertTrue(result.isPublished());
        assertFalse(result.isRedeemed());

        verify(repository, times(1)).save(any(Coupon.class));
    }

    @Test
    void shouldGetCouponSuccessfully() {
        UUID id = UUID.randomUUID();
        Coupon coupon = Coupon.reconstitute(id, "ABC123", "Discount", new BigDecimal("10.00"), LocalDateTime.now().plusDays(1), CouponStatus.ACTIVE, true, false);

        when(repository.findById(id)).thenReturn(Optional.of(coupon));

        Coupon result = getUseCase.execute(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("ABC123", result.getCode());
        assertEquals(CouponStatus.ACTIVE, result.getStatus());

        verify(repository, times(1)).findById(id);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenGettingDeletedCoupon() {
        UUID id = UUID.randomUUID();
        Coupon coupon = Coupon.reconstitute(id, "ABC123", "Discount", new BigDecimal("10.00"), LocalDateTime.now().plusDays(1), CouponStatus.DELETED, true, false);

        when(repository.findById(id)).thenReturn(Optional.of(coupon));

        assertThrows(ResourceNotFoundException.class, () -> getUseCase.execute(id));

        verify(repository, times(1)).findById(id);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenGettingNonExistingCoupon() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> getUseCase.execute(id));

        verify(repository, times(1)).findById(id);
    }

    @Test
    void shouldDeleteCouponSuccessfully() {
        UUID id = UUID.randomUUID();
        Coupon coupon = Coupon.reconstitute(id, "ABC123", "Discount", new BigDecimal("10.00"), LocalDateTime.now().plusDays(1), CouponStatus.ACTIVE, true, false);

        when(repository.findById(id)).thenReturn(Optional.of(coupon));
        when(repository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        deleteUseCase.execute(id);

        assertEquals(CouponStatus.DELETED, coupon.getStatus());
        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(coupon);
    }

    @Test
    void shouldThrowExceptionWhenDeletingAlreadyDeletedCoupon() {
        UUID id = UUID.randomUUID();
        Coupon coupon = Coupon.reconstitute(id, "ABC123", "Discount", new BigDecimal("10.00"), LocalDateTime.now().plusDays(1), CouponStatus.DELETED, true, false);

        when(repository.findById(id)).thenReturn(Optional.of(coupon));

        DomainException ex = assertThrows(DomainException.class, () -> deleteUseCase.execute(id));
        assertTrue(ex.getMessage().contains("already deleted"));

        verify(repository, times(1)).findById(id);
        verify(repository, never()).save(any(Coupon.class));
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenDeletingNonExistingCoupon() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> deleteUseCase.execute(id));

        verify(repository, times(1)).findById(id);
        verify(repository, never()).save(any(Coupon.class));
    }
}
