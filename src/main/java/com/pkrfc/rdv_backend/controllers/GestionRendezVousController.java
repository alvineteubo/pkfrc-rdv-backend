package com.pkrfc.rdv_backend.controllers;

import com.pkrfc.rdv_backend.models.dtos.ApiResponse;
import com.pkrfc.rdv_backend.models.dtos.requests.RendezVousRequest;
import com.pkrfc.rdv_backend.models.dtos.responses.RendezVousResponse;
import com.pkrfc.rdv_backend.models.entities.enums.StatutRendezVous;
import com.pkrfc.rdv_backend.services.inter.GestionRendezVousService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/api/rendez-vous")
@Tag(name = "gestion-rendez-vous", description = "Opérations sur les rendez-vous")
public class GestionRendezVousController {

    private final GestionRendezVousService rendezVousService;


    @Operation(summary = "Prendre un rendez-vous")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<RendezVousResponse>> prendreRendezVous(@Valid @RequestBody RendezVousRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, getMessage("success.operation"), rendezVousService.prendreRendezVous(request), new Date()));
    }

    @Operation(summary = "Récupérer un rendez-vous par ref")
    @GetMapping("/rendez-vous/{ref}")
    public ResponseEntity<ApiResponse<RendezVousResponse>> getByRef(@PathVariable String ref) {
        return ResponseEntity.ok(new ApiResponse<>(true, getMessage("success.operation"), rendezVousService.getRendezVousByRef(ref), new Date()));
    }

    @Operation(summary = "Lister les rendez-vous avec filtres et pagination")
    @GetMapping("/rendez-vous")
    public ResponseEntity<ApiResponse<Page<RendezVousResponse>>> getAllRendezVous(@RequestParam(required = false) String keyword,
                                                                        @RequestParam(required = false) String refClient,
                                                                        @RequestParam(required = false) String refResponsable,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size,
                                                                        @RequestParam(defaultValue = "DESC") String sort,
                                                                        @RequestParam(defaultValue = "1") int pagination,
                                                                        @RequestParam(defaultValue = "dateRdv") String sortBy) {
        Pageable pageable = pagination == 1 ? PageRequest.of(page, size, Sort.Direction.fromString(sort), sortBy) : Pageable.unpaged();
        return ResponseEntity.ok(new ApiResponse<>(true, getMessage("success.operation"), rendezVousService.getAllRendezVousByKeyword(keyword, refClient, refResponsable, pageable), new Date()));
    }

    @Operation(summary = "Changer le statut d'un rendez-vous")
    @PatchMapping("/{ref}/statut")
    public ResponseEntity<ApiResponse<RendezVousResponse>> changerStatut(@PathVariable("ref") String refRendezVous, @RequestParam StatutRendezVous statut){
        return ResponseEntity.ok(new ApiResponse<>(true,
                getMessage("success.operation"), rendezVousService.changerStatutRendezVous(refRendezVous, statut), new Date()));
    }

    @Operation(summary = "Ajouter un participant au rendez-vous")
    @PostMapping("/{refRdv}/participants/{refClient}")
    public ResponseEntity<ApiResponse<RendezVousResponse>> ajouterParticipant(@PathVariable String refRdv, @PathVariable String refClient) {
        return ResponseEntity.ok(new ApiResponse<>(true, getMessage("success.operation"), rendezVousService.ajouterParticipant(refRdv, refClient), new Date()));
    }

    @Operation(summary = "Retirer un participant du rendez-vous")
    @DeleteMapping("/{refRdv}/participants/{refClient}")
    public ResponseEntity<ApiResponse<RendezVousResponse>> retirerParticipant(@PathVariable String refRdv, @PathVariable String refClient) {
        return ResponseEntity.ok(new ApiResponse<>(true, getMessage("success.operation"), rendezVousService.retirerParticipant(refRdv, refClient), new Date()));
    }
}
