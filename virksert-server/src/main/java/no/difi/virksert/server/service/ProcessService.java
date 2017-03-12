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

package no.difi.virksert.server.service;

import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import no.difi.virksert.server.domain.Certificate;
import no.difi.virksert.server.domain.Participant;
import no.difi.virksert.server.domain.Process;
import no.difi.virksert.server.domain.ProcessRepository;
import no.difi.virksert.server.lang.ProcessNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

/**
 * @author erlend
 */
@Service
public class ProcessService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessService.class);

    @Autowired
    private ProcessRepository processRepository;

    @PostConstruct
    public void postConstruct() {
        if (processRepository.count() > 0)
            return;

        save(new Process("busdox-procid-ubl", "urn:www.cenbii.eu:profile:bii01:ver2.0", "Catalogue Only"));
        save(new Process("busdox-procid-ubl", "urn:www.cenbii.eu:profile:bii03:ver2.0", "Order Only"));
        save(new Process("busdox-procid-ubl", "urn:www.cenbii.eu:profile:bii04:ver2.0", "Invoice"));
        save(new Process("busdox-procid-ubl", "urn:www.cenbii.eu:profile:bii05:ver2.0", "Billing"));
        save(new Process("busdox-procid-ubl", "urn:www.cenbii.eu:profile:bii28:ver2.0", "Ordering"));
        save(new Process("busdox-procid-ubl", "urn:www.cenbii.eu:profile:bii30:ver2.0", "Despatch Advice"));
        save(new Process("busdox-procid-ubl", "urn:www.cenbii.eu:profile:biixx:ver2.0", "Credit Note Only"));
    }

    public Process get(ProcessIdentifier processIdentifier) throws ProcessNotFoundException {
        return Optional.ofNullable(processRepository.findByIdentifierAndScheme(
                processIdentifier.getIdentifier(), processIdentifier.getScheme().getValue()))
                .orElseThrow(() -> new ProcessNotFoundException(processIdentifier));
    }

    public List<Process> findAll() {
        return processRepository.findAll();
    }

    public Page<Process> findAll(int page) {
        return processRepository.findAll(new PageRequest(page, 20, Sort.Direction.ASC, "scheme", "identifier"));
    }

    public List<Process> findByCertificate(Certificate certificate) {
        return processRepository.findByCertificate(certificate);
    }

    public List<Process> findByParticipant(Participant participant) {
        return processRepository.findByParticipant(participant);
    }

    public void save(Process process) {
        processRepository.save(process);
    }
}
