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
