package com.pkrfc.rdv_backend.controllers;


import com.pkrfc.rdv_backend.models.dtos.ApiResponse;
import com.pkrfc.rdv_backend.models.dtos.responses.ServiceResponse;
import com.pkrfc.rdv_backend.services.inter.GestionServiceMetierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.pkrfc.rdv_backend.utils.I18nUtils.getMessage;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/service")
@Tag(name = "gestion-services", description = "Consultation des services")
public class GestionServiceMetierController {

    private final GestionServiceMetierService serviceMetierService;

    @Operation(summary = "Récupérer un service par ref")
    @GetMapping("/{ref_service}")
    public ResponseEntity<ApiResponse<ServiceResponse>> getByRef(@PathVariable String refService) {
        return ResponseEntity.ok(new ApiResponse<>(true,
                getMessage("success.operation"), serviceMetierService.getServiceByRef(refService), new Date()));
    }

    @Operation(summary = "Lister tous les services avec pagination et recherche")
    @GetMapping("/services")
    public ResponseEntity<ApiResponse<Page<ServiceResponse>>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam(defaultValue = "1") int pagination,
            @RequestParam(defaultValue = "nom") String sortBy) {
        Pageable pageable = pagination == 1
                ? PageRequest.of(page, size, Sort.Direction.fromString(sort), sortBy)
                : Pageable.unpaged();
        return ResponseEntity.ok(new ApiResponse<>(true,
                getMessage("success.operation"), serviceMetierService.getAllServices(keyword, pageable), new Date()));
    }
}