package com.pkrfc.rdv_backend.controllers;

import com.pkrfc.rdv_backend.models.dtos.ApiResponse;
import com.pkrfc.rdv_backend.models.dtos.responses.PlageHoraireResponse;
import com.pkrfc.rdv_backend.services.inter.GestionPlageHoraireService;
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
@RequestMapping("/api/plages-horaire")
@Tag(name = "gestion-plages-horaires", description = "Consultation des plages horaires")
public class GestionPlageHoraireController {

    private final GestionPlageHoraireService plageHoraireService;


    @Operation(summary = "Récupérer une plage horaire par id")
    @GetMapping("/{id_plage}")
    public ResponseEntity<ApiResponse<PlageHoraireResponse>> getById(@PathVariable("id_plage") String idPlage) {
        return ResponseEntity.ok(new ApiResponse<>(true,
                getMessage("success.operation"), plageHoraireService.getPlageHoraireById(idPlage), new Date()));
    }

    @Operation(summary = "Lister toutes les plages horaires")
    @GetMapping("/plage_horaires")
    public ResponseEntity<ApiResponse<Page<PlageHoraireResponse>>> getAllPlageHoraires(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ASC") String sort,
            @RequestParam(defaultValue = "1") int pagination,
            @RequestParam(defaultValue = "heureDebut") String sortBy) {
        Pageable pageable = pagination == 1
                ? PageRequest.of(page, size, Sort.Direction.fromString(sort), sortBy)
                : Pageable.unpaged();
        return ResponseEntity.ok(new ApiResponse<>(true,
                getMessage("success.operation"), plageHoraireService.getAllPlagesHoraires(pageable), new Date()));
    }
}