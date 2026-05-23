package com.pkrfc.rdv_backend.models.dtos.responses;

public record ServiceResponse(
        String ref_service,
        String code,
        String nom,
        String description
) {
}
