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

import no.difi.certvalidator.Validator;
import no.difi.bcp.server.domain.CertificateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author erlend
 */
@Service
public class ValidationService {

    private static Logger logger = LoggerFactory.getLogger(ValidationService.class);

    @Autowired
    private Validator validator;

    @Autowired
    private CertificateRepository certificateRepository;

    @Scheduled(cron = "0 0 3 * * *")
    // @Scheduled(fixedDelay = 1000)
    public void run() {
        logger.info("Validating registered certificates.");

        /*
        for (Certificate certificate : certificateRepository.listValid()) {
            if (!validator.isValid(certificate.getCertificate())) {
                certificate.setRevoked(System.currentTimeMillis());
                certificateRepository.insert(certificate);
            }
        }
        */
    }
}
