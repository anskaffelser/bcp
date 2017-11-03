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

package no.difi.bcp.server.config;

import no.difi.bcp.lang.BcpException;
import no.difi.bcp.security.BusinessCertificateValidator;
import no.difi.certvalidator.ValidatorGroup;
import no.difi.certvalidator.api.CrlFetcher;
import no.difi.certvalidator.api.ErrorHandler;
import no.difi.certvalidator.util.SimpleCachingCrlFetcher;
import no.difi.certvalidator.util.SimpleCrlCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author erlend
 */
@Configuration
@PropertySource(value = "bcp.properties", ignoreResourceNotFound = true)
public class BusinessCertificateConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessCertificateConfig.class);

    @Value("${bcp.mode:production}")
    private String mode;

    @PostConstruct
    public void postConstruct() {
        LOGGER.info("Mode: {}", mode);
    }

    @Bean
    public BusinessCertificateValidator getBusinessCertificate(CrlFetcher crlFetcher) throws BcpException {
        Map<String, Object> values = new HashMap<>();
        values.put("crlFetcher", crlFetcher);
        values.put("#errorhandler", (ErrorHandler) e -> LOGGER.info(e.getMessage(), e));

        return BusinessCertificateValidator.of(mode, values);
    }

    @Bean
    public ValidatorGroup getValidator(BusinessCertificateValidator businessCertificateValidator) throws BcpException {
        return businessCertificateValidator.getValidator();
    }

    @Bean
    public CrlFetcher createCrlFetcher() {
        return new SimpleCachingCrlFetcher(new SimpleCrlCache());
    }
}
