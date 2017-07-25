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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author erlend
 */
@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    public Application get(String identifier) {
        return applicationRepository.findByIdentifier(identifier);
    }

    public List<Application> findByParticipant(Participant participant) {
        return applicationRepository.findByParticipant(participant);
    }

    @Transactional
    public Application save(Application application) {
        return applicationRepository.save(application);
    }

}
