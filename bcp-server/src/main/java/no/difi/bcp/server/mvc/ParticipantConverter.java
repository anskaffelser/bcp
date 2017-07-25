package no.difi.bcp.server.mvc;

import no.difi.vefa.peppol.common.lang.PeppolParsingException;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.bcp.server.domain.Participant;
import no.difi.bcp.server.lang.ParticipantNotFoundException;
import no.difi.bcp.server.service.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * @author erlend
 */
@Component
public class ParticipantConverter implements Converter<String, Participant> {

    @Autowired
    private ParticipantService participantService;

    @Override
    public Participant convert(String s) {
        try {
            return participantService.get(ParticipantIdentifier.parse(s));
        } catch (PeppolParsingException | ParticipantNotFoundException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }
}
