package com.pkrfc.rdv_backend.services.inter;

import com.pkrfc.rdv_backend.models.dtos.requests.ClientRequest;
import com.pkrfc.rdv_backend.models.dtos.responses.ClientResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GestionClientService {
    ClientResponse createOrUpdateClient(ClientRequest request);
    ClientResponse getClientByRef(String ref);
    Page<ClientResponse> getAllClientsByKeyword(String keyword, Pageable pageable);
    void deleteClient(String ref);
}
