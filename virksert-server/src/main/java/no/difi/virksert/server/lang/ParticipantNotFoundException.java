package no.difi.virksert.server.lang;

import no.difi.vefa.peppol.common.model.ParticipantIdentifier;

/**
 * @author erlend
 */
public class ParticipantNotFoundException extends VirksertServerException {

    public ParticipantNotFoundException(String message) {
        super(message);
    }

    public ParticipantNotFoundException(ParticipantIdentifier participantIdentifier) {
        this(String.format("Participant '%s' not found.", participantIdentifier));
    }
}
