package com.pkrfc.rdv_backend.services.inter;

import com.pkrfc.rdv_backend.models.dtos.requests.UtilisateurRequest;
import com.pkrfc.rdv_backend.models.dtos.responses.UtilisateurResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GestionUtilisateurService {
    UtilisateurResponse createOrUpdateUtilisateur(UtilisateurRequest request);
    UtilisateurResponse getUtilisateurByRef(String ref);
    UtilisateurResponse getUtilisateurByEmail(String email);
    Page<UtilisateurResponse> getAllUtilisateurByKeyword(String keyword, Pageable pageable);
    void deleteUtilisateur(String ref);
}
