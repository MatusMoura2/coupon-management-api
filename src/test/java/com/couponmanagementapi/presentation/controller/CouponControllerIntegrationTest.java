package com.couponmanagementapi.presentation.controller;

import com.couponmanagementapi.domain.model.Coupon;
import com.couponmanagementapi.domain.model.CouponStatus;
import com.couponmanagementapi.infrastructure.persistence.CouponJpaEntity;
import com.couponmanagementapi.infrastructure.persistence.SpringDataCouponRepository;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CouponControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpringDataCouponRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void shouldCreateCouponSuccessfully() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("code", "ABC-123");
        body.put("description", "Valid discount");
        body.put("discountValue", 10.5);
        body.put("expirationDate", LocalDateTime.now().plusDays(2).toString());
        body.put("published", true);

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code", is("ABC123")))
                .andExpect(jsonPath("$.description", is("Valid discount")))
                .andExpect(jsonPath("$.discountValue", is(10.5)))
                .andExpect(jsonPath("$.status", is("ACTIVE")))
                .andExpect(jsonPath("$.published", is(true)))
                .andExpect(jsonPath("$.redeemed", is(false)));
    }

    @Test
    void shouldReturnBadRequestWhenCreateCouponHasValidationErrors() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("code", "   "); // Empty code
        body.put("description", ""); // Empty description
        body.put("discountValue", 0.4); // Less than 0.5
        body.put("expirationDate", LocalDateTime.now().minusDays(1).toString()); // Past date

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Error")));
    }

    @Test
    void shouldGetCouponByIdSuccessfully() throws Exception {
        Coupon coupon = Coupon.create("XYZ-987", "Some Coupon", new BigDecimal("15.0"), LocalDateTime.now().plusDays(2), false);
        CouponJpaEntity entity = CouponJpaEntity.fromDomain(coupon);
        CouponJpaEntity saved = repository.save(entity);

        mockMvc.perform(get("/coupon/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId().toString())))
                .andExpect(jsonPath("$.code", is("XYZ987")))
                .andExpect(jsonPath("$.status", is("ACTIVE")));
    }

    @Test
    void shouldReturnNotFoundWhenCouponDoesNotExist() throws Exception {
        mockMvc.perform(get("/coupon/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenCouponIsSoftDeleted() throws Exception {
        Coupon coupon = Coupon.create("XYZ-987", "Deleted Coupon", new BigDecimal("15.0"), LocalDateTime.now().plusDays(2), false);
        coupon.softDelete();
        CouponJpaEntity entity = CouponJpaEntity.fromDomain(coupon);
        CouponJpaEntity saved = repository.save(entity);

        mockMvc.perform(get("/coupon/" + saved.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSoftDeleteCouponSuccessfully() throws Exception {
        Coupon coupon = Coupon.create("DEL-123", "To be deleted", new BigDecimal("5.0"), LocalDateTime.now().plusDays(2), true);
        CouponJpaEntity entity = CouponJpaEntity.fromDomain(coupon);
        CouponJpaEntity saved = repository.save(entity);

        mockMvc.perform(delete("/coupon/" + saved.getId()))
                .andExpect(status().isNoContent());

        // Verify in DB that it is soft deleted
        CouponJpaEntity dbCoupon = repository.findById(saved.getId()).orElseThrow();
        assertEquals(CouponStatus.DELETED, dbCoupon.getStatus());
    }

    @Test
    void shouldReturnBadRequestWhenDeletingAlreadyDeletedCoupon() throws Exception {
        Coupon coupon = Coupon.create("DEL-123", "To be deleted", new BigDecimal("5.0"), LocalDateTime.now().plusDays(2), true);
        coupon.softDelete();
        CouponJpaEntity entity = CouponJpaEntity.fromDomain(coupon);
        CouponJpaEntity saved = repository.save(entity);

        mockMvc.perform(delete("/coupon/" + saved.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Coupon is already deleted")));
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingCoupon() throws Exception {
        mockMvc.perform(delete("/coupon/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldLoadApiDocsSuccessfully() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk());
    }
}
