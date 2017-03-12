package no.difi.virksert.server.service;

import no.difi.virksert.server.domain.Registration;
import no.difi.virksert.server.domain.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author erlend
 */
@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    public void save(Registration registration) {
        registrationRepository.save(registration);
    }
}
