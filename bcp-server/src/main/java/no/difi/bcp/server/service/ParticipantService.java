/*
 * Copyright 2017 Norwegian Agency for Public Management and eGovernment (Difi)
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package no.difi.bcp.server.service;

import no.difi.bcp.server.domain.Participant;
import no.difi.bcp.server.domain.ParticipantRepository;
import no.difi.bcp.server.form.SepForm;
import no.difi.bcp.server.lang.BcpServerException;
import no.difi.bcp.server.lang.ParticipantNotFoundException;
import no.difi.bcp.server.util.DatahotelOrganization;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author erlend
 */
@Service
public class ParticipantService {

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private DatahotelService datahotelService;

    @Transactional(readOnly = true)
    public Participant get(ParticipantIdentifier participantIdentifier) throws ParticipantNotFoundException {
        return Optional.ofNullable(participantRepository.findByIdentifierAndScheme(
                participantIdentifier.getIdentifier(), participantIdentifier.getScheme().getValue()))
                .orElseThrow(() -> new ParticipantNotFoundException(participantIdentifier));
    }

    @Transactional(readOnly = true)
    public Page<Participant> findAll(int page) {
        return participantRepository.findAll(new PageRequest(page, 20, Sort.Direction.ASC, "name"));
    }

    @Transactional(readOnly = true)
    public List<Participant> findByOwnership(Participant participant) {
        return participantRepository.findByOwnership(participant);
    }

    @Transactional
    public Participant save(Participant participant) throws BcpServerException {
        if (participant.getId() == 0) {
            if (participant.getIdentifier().startsWith("9908:")) {
                Optional<DatahotelOrganization> org =
                        datahotelService.findByIdentifier(participant.getIdentifier().substring(5));
                org.map(DatahotelOrganization::getNavn)
                        .ifPresent(participant::setName);
            }
        }

        return participantRepository.save(participant);
    }

    @Transactional
    public Participant createSep(Participant parent, SepForm form) {
        Participant participant = form.update(new Participant());
        participant.setParent(parent);
        participant.setScheme(parent.getScheme());
        participant.setIdentifier(String.format("%s:%s", parent.getIdentifier(), form.getIdentifier()));
        participantRepository.save(participant);

        return participant;
    }
}
