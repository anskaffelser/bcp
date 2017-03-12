package no.difi.virksert.server.service;

import no.difi.certvalidator.Validator;
import no.difi.virksert.server.domain.Certificate;
import no.difi.virksert.server.domain.CertificateRepository;
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
