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

import no.difi.bcp.api.Role;
import no.difi.bcp.server.domain.*;
import no.difi.bcp.server.domain.Process;
import no.difi.bcp.server.form.ApplicationProcessForm;
import no.difi.bcp.server.lang.NoCertificatesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author erlend
 */
@Service
public class RegistrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationService.class);

    @Autowired
    private RegistrationRepository registrationRepository;

    @Transactional(readOnly = true)
    public List<Registration> findByApplication(Application application) {
        return registrationRepository
                .findByApplication(application, new Sort(Sort.Direction.ASC, "process.domain.title", "process.title"));
    }

    @Transactional
    public void save(Registration registration) {
        registrationRepository.save(registration);
    }

    @Transactional
    public void update(Application application, ApplicationProcessForm form) {
        List<Registration> registrations = registrationRepository.findByApplication(application, null);

        List<ProcessSet> existingSets = registrations.stream()
                .map(Registration::toProcessSet)
                .collect(Collectors.toList());

        for (ProcessSet applied : form.getProcesses()) {
            if (existingSets.contains(applied)) {
                LOGGER.info("Found: {}", applied);
                existingSets.remove(applied);
            } else {
                LOGGER.info("Add: {}", applied);
                registrationRepository.save(new Registration(application, applied));
            }
        }

        for (ProcessSet removed : existingSets) {
            LOGGER.info("Remove: {}", removed);
            registrations.stream()
                    .filter(removed::equalsRegistration)
                    .findFirst()
                    .ifPresent(registrationRepository::delete);
        }
    }

    @Transactional(readOnly = true)
    public List<Registration> findProcesses(Participant participant) {
        return registrationRepository.findProcesses(participant);
    }

    @Transactional(readOnly = true)
    public List<Certificate> findCertificates(Participant participant, Process process, Role role)
            throws NoCertificatesException {
        List<Certificate> certificates = registrationRepository.findCertificates(participant, process, role);

        if (certificates.size() == 0)
            throw new NoCertificatesException("No certificates found.");

        return certificates;
    }
}
