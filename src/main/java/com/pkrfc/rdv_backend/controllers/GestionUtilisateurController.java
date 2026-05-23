package com.pkrfc.rdv_backend.controllers;


import com.pkrfc.rdv_backend.models.dtos.ApiResponse;
import com.pkrfc.rdv_backend.models.dtos.requests.UtilisateurRequest;
import com.pkrfc.rdv_backend.models.dtos.responses.UtilisateurResponse;
import com.pkrfc.rdv_backend.services.inter.GestionUtilisateurService;
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
@RequestMapping("/api/utilisateur")
@Tag(name = "gestion-utilisateurs", description = "Opérations sur les utilisateurs")
public class GestionUtilisateurController {

    private final GestionUtilisateurService utilisateurService;


    @Operation(summary = "Créer ou mettre à jour un utilisateur")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<UtilisateurResponse>> createOrUpdate(
            @Valid @RequestBody UtilisateurRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true,
                getMessage("success.operation"), utilisateurService.createOrUpdateUtilisateur(request), new Date()));
    }

    @Operation(summary = "Récupérer un utilisateur par ref")
    @GetMapping("/{ref_utilisateur}")
    public ResponseEntity<ApiResponse<UtilisateurResponse>> getByRef(@PathVariable String refUtilisateur) {
        return ResponseEntity.ok(new ApiResponse<>(true,
                getMessage("success.operation"), utilisateurService.getUtilisateurByRef(refUtilisateur), new Date()));
    }

    @Operation(summary = "Récupérer un utilisateur par email")
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UtilisateurResponse>> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok(new ApiResponse<>(true,
                getMessage("success.operation"), utilisateurService.getUtilisateurByEmail(email), new Date()));
    }

    @Operation(summary = "Lister les utilisateurs avec pagination et recherche")
    @GetMapping("/utilisateurs")
    public ResponseEntity<ApiResponse<Page<UtilisateurResponse>>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String sort,
            @RequestParam(defaultValue = "1") int pagination,
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        Pageable pageable = pagination == 1
                ? PageRequest.of(page, size, Sort.Direction.fromString(sort), sortBy)
                : Pageable.unpaged();
        return ResponseEntity.ok(new ApiResponse<>(true,
                getMessage("success.operation"), utilisateurService.getAllUtilisateurByKeyword(keyword, pageable), new Date()));
    }

    @Operation(summary = "Supprimer un utilisateur")
    @DeleteMapping("/{ref_utilisateur}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String refUtilisateur) {
        utilisateurService.deleteUtilisateur(refUtilisateur);
        return ResponseEntity.ok(new ApiResponse<>(true,
                getMessage("success.utilisateur.deleted"), null, new Date()));
    }
}
