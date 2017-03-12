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

package no.difi.virksert.server.config;

import no.difi.certvalidator.Validator;
import no.difi.virksert.lang.BusinessCertificateException;
import no.difi.virksert.security.BusinessCertificateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;

/**
 * @author erlend
 */
@Configuration
@PropertySource(value = "virksert.properties", ignoreResourceNotFound = true)
public class BusinessCertificateConfig {

    private static Logger logger = LoggerFactory.getLogger(BusinessCertificateConfig.class);

    @Value("${virksert.mode:production}")
    private String mode;

    @PostConstruct
    public void postConstruct() {
        logger.info("Mode: {}", mode);
    }

    @Bean
    public BusinessCertificateValidator getBusinessCertificate() throws BusinessCertificateException {
        return BusinessCertificateValidator.of(mode);
    }

    @Bean
    public Validator getValidator(BusinessCertificateValidator businessCertificateValidator) throws BusinessCertificateException {
        return businessCertificateValidator.getValidator();
    }
}
