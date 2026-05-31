package com.couponmanagementapi.presentation.controller;

import com.couponmanagementapi.application.usecase.CreateCouponUseCase;
import com.couponmanagementapi.application.usecase.DeleteCouponUseCase;
import com.couponmanagementapi.application.usecase.GetCouponUseCase;
import com.couponmanagementapi.domain.model.Coupon;
import com.couponmanagementapi.presentation.dto.CouponRequest;
import com.couponmanagementapi.presentation.dto.CouponResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
@Tag(name = "Coupon", description = "API para gerenciamento de cupons de desconto")
public class CouponController {

    private final CreateCouponUseCase createCouponUseCase;
    private final GetCouponUseCase getCouponUseCase;
    private final DeleteCouponUseCase deleteCouponUseCase;

    @PostMapping
    @Operation(summary = "Cadastra um novo cupom", description = "Cadastra um cupom aplicando as regras de negócio de validação de desconto, expiração e limpeza de código.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cupom criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos ou violação de regras de negócio")
    })
    public ResponseEntity<CouponResponse> createCoupon(@Valid @RequestBody CouponRequest request) {
        Coupon coupon = createCouponUseCase.execute(
                request.getCode(),
                request.getDescription(),
                request.getDiscountValue(),
                request.getExpirationDate(),
                request.getPublished() != null ? request.getPublished() : false
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(CouponResponse.fromDomain(coupon));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um cupom pelo ID", description = "Retorna os detalhes de um cupom que não esteja deletado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cupom encontrado"),
            @ApiResponse(responseCode = "404", description = "Cupom não encontrado ou deletado")
    })
    public ResponseEntity<CouponResponse> getCouponById(@PathVariable UUID id) {
        Coupon coupon = getCouponUseCase.execute(id);
        return ResponseEntity.ok(CouponResponse.fromDomain(coupon));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deleta logicamente um cupom", description = "Realiza o soft delete do cupom, marcando seu status como DELETED.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cupom deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Cupom já deletado anteriormente"),
            @ApiResponse(responseCode = "404", description = "Cupom não encontrado")
    })
    public void deleteCoupon(@PathVariable UUID id) {
        deleteCouponUseCase.execute(id);
    }
}
