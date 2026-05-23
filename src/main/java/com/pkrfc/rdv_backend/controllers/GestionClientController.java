package com.pkrfc.rdv_backend.controllers;

import com.pkrfc.rdv_backend.models.dtos.ApiResponse;
import com.pkrfc.rdv_backend.models.dtos.requests.ClientRequest;
import com.pkrfc.rdv_backend.models.dtos.responses.ClientResponse;
import com.pkrfc.rdv_backend.services.inter.GestionClientService;
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
@RequestMapping("/api/client")
@Tag(name = "gestion-clients", description = "Opérations sur les clients")
public class GestionClientController {

    private final GestionClientService clientService;

    @Operation(summary = "Créer ou mettre à jour un client")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ClientResponse>> createOrUpdate(
            @Valid @RequestBody ClientRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true,
                getMessage("success.operation"), clientService.createOrUpdateClient(request), new Date()));
    }

    @Operation(summary = "Récupérer un client par ref")
    @GetMapping("/{ref_client}")
    public ResponseEntity<ApiResponse<ClientResponse>> getByRef(@PathVariable String refClient) {
        return ResponseEntity.ok(new ApiResponse<>(true, getMessage("success.operation"),
                clientService.getClientByRef(refClient), new Date()));
    }

    @Operation(summary = "Lister les clients avec pagination et recherche")
    @GetMapping("/clients")
    public ResponseEntity<ApiResponse<Page<ClientResponse>>> getAll(
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
                getMessage("success.operation"), clientService.getAllClientsByKeyword(keyword, pageable), new Date()));
    }

    @Operation(summary = "Supprimer un client")
    @DeleteMapping("/{ref_client}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String refClient) {
        clientService.deleteClient(refClient);
        return ResponseEntity.ok(new ApiResponse<>(true, getMessage("success.client.deleted"), null, new Date()));
    }
}