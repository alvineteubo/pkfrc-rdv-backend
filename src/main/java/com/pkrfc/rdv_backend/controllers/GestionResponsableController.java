package com.pkrfc.rdv_backend.controllers;

import com.pkrfc.rdv_backend.models.dtos.ApiResponse;
import com.pkrfc.rdv_backend.models.dtos.requests.ResponsableRequest;
import com.pkrfc.rdv_backend.models.dtos.responses.ResponsableResponse;
import com.pkrfc.rdv_backend.services.inter.GestionResponsableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
@RequestMapping("/api/responsable")
@Tag(name = "gestion-responsables", description = "Opérations sur les responsables")
public class GestionResponsableController {

    private final GestionResponsableService responsableService;

    @Operation(summary = "Créer ou mettre à jour un responsable")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ResponsableResponse>> createOrUpdate(
            @Valid @RequestBody ResponsableRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true,
                getMessage("success.operation"), responsableService.createOrUpdateResponsable(request), new Date()));
    }

    @Operation(summary = "Récupérer un responsable par ref")
    @GetMapping("/{ref_responsable}")
    public ResponseEntity<ApiResponse<ResponsableResponse>> getByRef(@PathVariable String refResponsable) {
        return ResponseEntity.ok(new ApiResponse<>(true, getMessage("success.operation"), responsableService.getResponsableByRef(refResponsable), new Date()));
    }

    @Operation(summary = "Lister les responsables avec pagination, recherche et filtre service")
    @GetMapping("/responsables")
    public ResponseEntity<ApiResponse<Page<ResponsableResponse>>> getAllResponsables(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String refService,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam(defaultValue = "1") int pagination,
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        Pageable pageable = pagination == 1
                ? PageRequest.of(page, size, Sort.Direction.fromString(sort), sortBy)
                : Pageable.unpaged();
        return ResponseEntity.ok(new ApiResponse<>(true,
                getMessage("success.operation"), responsableService.getAllResponsablesByKeyword(keyword, refService, pageable), new Date()));
    }

    @Operation(summary = "Supprimer un responsable")
    @DeleteMapping("/{ref_responsable}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String refResponsable) {
        responsableService.supprimerResponsable(refResponsable);
        return ResponseEntity.ok(new ApiResponse<>(true, getMessage("success.responsable.deleted"), null, new Date()));
    }
}
