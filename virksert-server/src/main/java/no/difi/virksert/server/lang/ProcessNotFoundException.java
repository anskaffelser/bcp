package no.difi.virksert.server.lang;

import no.difi.vefa.peppol.common.model.ProcessIdentifier;

/**
 * @author erlend
 */
public class ProcessNotFoundException extends VirksertServerException {

    public ProcessNotFoundException(String message) {
        super(message);
    }

    public ProcessNotFoundException(ProcessIdentifier processIdentifier) {
        this(String.format("Process '%s' not found.", processIdentifier));
    }
}
