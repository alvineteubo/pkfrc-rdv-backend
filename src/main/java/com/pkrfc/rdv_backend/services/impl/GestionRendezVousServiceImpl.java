package com.pkrfc.rdv_backend.services.impl;

import com.pkrfc.rdv_backend.exceptions.BadRequestException;
import com.pkrfc.rdv_backend.exceptions.ResourceNotFoundException;
import com.pkrfc.rdv_backend.models.dtos.requests.RendezVousRequest;
import com.pkrfc.rdv_backend.models.dtos.responses.ClientResponse;
import com.pkrfc.rdv_backend.models.dtos.responses.RendezVousResponse;
import com.pkrfc.rdv_backend.models.entities.*;
import com.pkrfc.rdv_backend.models.entities.enums.StatutRendezVous;
import com.pkrfc.rdv_backend.models.mappers.ClientMapper;
import com.pkrfc.rdv_backend.models.mappers.RendezVousMapper;
import com.pkrfc.rdv_backend.models.repositories.*;
import com.pkrfc.rdv_backend.services.inter.GestionRendezVousService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.pkrfc.rdv_backend.utils.I18nUtils.getMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class GestionRendezVousServiceImpl implements GestionRendezVousService {

    private final RendezVousRepository rendezVousRepository;
    private final RendezVousParticipantRepository participantRepository;
    private final ClientRepository clientRepository;
    private final ResponsableRepository responsableRepository;
    private final ServiceMetierRepository serviceMetierRepository;
    private final PlageHoraireRepository plageHoraireRepository;


    private void validerDateRdv(LocalDateTime dateRdv) {
        LocalDate dateMinimale = LocalDate.now().plusDays(2);
        if (dateRdv.toLocalDate().isBefore(dateMinimale)) {
            throw new BadRequestException(getMessage("rdv.too.soon"));
        }
    }

    private Client validerClient(String refClient) {
        return clientRepository.findById(refClient)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "ref", refClient));
    }

    private Responsable validerResponsable(String refResponsable) {
        return responsableRepository.findById(refResponsable)
                .orElseThrow(() -> new ResourceNotFoundException("Responsable", "ref", refResponsable));
    }

    private ServiceMetier validerService(String refService) {
        return serviceMetierRepository.findById(refService)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceMetier", "ref", refService));
    }

    private PlageHoraire deduirePlageHoraire(LocalDateTime dateRdv) {
        LocalTime heure = dateRdv.toLocalTime();
        return plageHoraireRepository.findByHeureDebut(heure)
                .orElseThrow(() -> new BadRequestException(getMessage("rdv.plage.not.found")));
    }

    private void validerDisponibiliteResponsable(Responsable responsable, PlageHoraire plageHoraire, LocalDateTime dateRdv) {
        List<RendezVous> rdvExistants = rendezVousRepository
                .findByResponsableAndPlageAndDate(responsable.getRefResponsable(), plageHoraire, dateRdv);
        if (!rdvExistants.isEmpty()) {
            throw new BadRequestException(getMessage("rdv.responsable.unavailable"));
        }
    }

    private void validerNombreParticipants(RendezVous rendezVous) {
        long count = participantRepository.countByRendezVous(rendezVous);
        if (count >= 2) {
            throw new BadRequestException(getMessage("rdv.max.participants"));
        }
    }

    private void validerClientNonInscrit(RendezVous rendezVous, String refClient) {
        long count = participantRepository.countByRendezVousAndRefClient(rendezVous, refClient);
        if (count > 0) {
            throw new BadRequestException(getMessage("rdv.client.already.registered"));
        }
    }

    private RendezVousResponse buildResponse(RendezVous rendezVous) {
        List<ClientResponse> participants = rendezVous.getParticipants()
                .stream()
                .map(p -> ClientMapper.toResponse(p.getClient()))
                .toList();
        return RendezVousMapper.toResponse(rendezVous, participants);
    }


    @Override
    @Transactional
    public RendezVousResponse prendreRendezVous(RendezVousRequest request) {

        validerDateRdv(request.dateRdv());
        Client client = validerClient(request.refClient());
        Responsable responsable = validerResponsable(request.refResponsable());
        ServiceMetier serviceMetier = validerService(request.refService());
        PlageHoraire plageHoraire = deduirePlageHoraire(request.dateRdv());
        validerDisponibiliteResponsable(responsable, plageHoraire, request.dateRdv());

        RendezVous rendezVous = RendezVousMapper.toEntity(request, responsable, serviceMetier, plageHoraire);
        rendezVous = rendezVousRepository.save(rendezVous);
        RendezVousParticipant participant = RendezVousParticipant.builder()
                .rendezVous(rendezVous)
                .client(client)
                .build();
        participantRepository.save(participant);
        return buildResponse(rendezVous);
    }


    @Transactional
    @Override
    public RendezVousResponse changerStatutRendezVous(String ref, StatutRendezVous nouveauStatut) {
        RendezVous rendezVous = rendezVousRepository.findById(ref)
                .orElseThrow(() -> new ResourceNotFoundException("RendezVous", "ref", ref));

        switch (rendezVous.getStatut()) {
            case ANNULE -> throw new BadRequestException(getMessage("rdv.already.cancelled"));
            case CONFIRME -> {
                if (nouveauStatut == StatutRendezVous.CONFIRME) {
                    throw new BadRequestException(getMessage("rdv.already.confirmed"));
                }
                rendezVous.setStatut(nouveauStatut);
            }
            case EN_ATTENTE -> rendezVous.setStatut(nouveauStatut);
        }

        rendezVous = rendezVousRepository.save(rendezVous);
        log.info("Statut RDV {} changé vers : {}", ref, nouveauStatut);
        return buildResponse(rendezVous);
    }


@Override
@Transactional
public RendezVousResponse ajouterParticipant(String refRdv, String refClient) {
    RendezVous rendezVous = rendezVousRepository.findById(refRdv)
            .orElseThrow(() -> new ResourceNotFoundException("RendezVous", "ref", refRdv));

    if (rendezVous.getStatut() == StatutRendezVous.ANNULE) {
        throw new BadRequestException(getMessage("rdv.already.cancelled"));
    }
    Client client = validerClient(refClient);
    validerNombreParticipants(rendezVous);
    validerClientNonInscrit(rendezVous, refClient);

    RendezVousParticipant participant = RendezVousParticipant.builder()
            .rendezVous(rendezVous)
            .client(client)
            .build();
    participantRepository.save(participant);
    rendezVous = rendezVousRepository.findById(refRdv).orElseThrow();
    log.info("Participant {} ajouté au RDV {}", refClient, refRdv);
    return buildResponse(rendezVous);
}

@Override
@Transactional
public RendezVousResponse retirerParticipant(String refRdv, String refClient) {
    RendezVous rendezVous = rendezVousRepository.findById(refRdv)
            .orElseThrow(() -> new ResourceNotFoundException("RendezVous", "ref", refRdv));
    if (rendezVous.getStatut() == StatutRendezVous.ANNULE) {
        throw new BadRequestException(getMessage("rdv.already.cancelled"));
    }
    RendezVousParticipant participant = participantRepository.findByRendezVousAndRefClient(rendezVous, refClient)
            .orElseThrow(() -> new ResourceNotFoundException("Participant", "refClient", refClient));

    participantRepository.delete(participant);
    participantRepository.flush();
    rendezVous = rendezVousRepository.findById(refRdv).orElseThrow();
    return buildResponse(rendezVous);
}

@Override
@Transactional(readOnly = true)
public RendezVousResponse getRendezVousByRef(String ref) {
    RendezVous rendezVous = rendezVousRepository.findById(ref)
            .orElseThrow(() -> new ResourceNotFoundException("RendezVous", "ref", ref));
    return buildResponse(rendezVous);
}

@Transactional(readOnly = true)
@Override
public Page<RendezVousResponse> getAllRendezVousByKeyword(String keyword, String refClient, String refResponsable, Pageable pageable) {
    return RendezVousMapper.buildPageFromEntities(
            rendezVousRepository.getAllRendezVousByKeyword(keyword, refClient, refResponsable, pageable)
    );
}

}