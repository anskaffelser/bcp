/*
 *  Copyright 2017 Norwegian Agency for Public Management and eGovernment (Difi)
 *
 *  Licensed under the EUPL, Version 1.1 or – as soon they
 *  will be approved by the European Commission - subsequent
 *  versions of the EUPL (the "Licence");
 *
 *  You may not use this work except in compliance with the Licence.
 *
 *  You may obtain a copy of the Licence at:
 *
 *  https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 *  Unless required by applicable law or agreed to in
 *  writing, software distributed under the Licence is
 *  distributed on an "AS IS" basis,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *  express or implied.
 *  See the Licence for the specific language governing
 *  permissions and limitations under the Licence.
 */

package no.difi.bcp.server.service;

import no.difi.bcp.server.domain.Application;
import no.difi.bcp.server.domain.ApplicationRepository;
import no.difi.bcp.server.domain.Participant;
import no.difi.bcp.server.form.ApplicationCertificateForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author erlend
 */
@Service
public class ApplicationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationService.class);

    @Autowired
    private ApplicationRepository applicationRepository;

    @Transactional(readOnly = true)
    public Application get(String identifier) {
        return applicationRepository.findByIdentifier(identifier);
    }

    @Transactional(readOnly = true)
    public boolean isEnabled(Application application, Participant participant) {
        return applicationRepository.countByIdAndCustomers(application.getId(), participant) != 0;
    }

    @Transactional(readOnly = true)
    public List<Application> findByParticipant(Participant participant) {
        return applicationRepository.findByParticipant(participant);
    }

    @Transactional(readOnly = true)
    public List<Application> findByCustomer(Participant participant) {
        return applicationRepository.findByCustomers(participant, new Sort(Sort.Direction.ASC, "title"));
    }

    @Transactional
    public Application save(Application application) {
        return applicationRepository.save(application);
    }

    @Transactional
    public void update(Application application, ApplicationCertificateForm form) {
        applicationRepository.save(form.update(application));
    }

    @Transactional
    public void enableCustomer(Application application, Participant participant) {
        LOGGER.info("Enable customer '{}' for '{}'.", participant.getName(), application.getTitle());
        // application.getCustomers().add(participant);
        // applicationRepository.save(application);
    }

    @Transactional
    public void disableCustomer(Application application, Participant participant) {
        LOGGER.info("Disable customer '{}' for '{}'.", participant.getName(), application.getTitle());
        // application.getCustomers().remove(participant);
        // applicationRepository.save(application);
    }
}
