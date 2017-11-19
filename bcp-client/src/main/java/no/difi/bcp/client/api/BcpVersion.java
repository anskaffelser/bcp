package no.difi.bcp.client.api;

import no.difi.bcp.api.Role;
import no.difi.bcp.client.lang.BcpClientException;
import no.difi.bcp.client.model.ParticipantLookup;
import no.difi.bcp.client.model.ProcessLookup;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import org.w3c.dom.Document;

import java.io.IOException;

/**
 * @author erlend
 */
public interface BcpVersion {

    String generatePath(ParticipantIdentifier participantIdentifier);

    String generatePath(ParticipantIdentifier participantIdentifier,
                        ProcessIdentifier processIdentifier,
                        Role role);

    ParticipantLookup parseParticipantLookup(Document document) throws BcpClientException;

    ProcessLookup parseProcessLookup(Document document) throws BcpClientException;

    StatusCode parseStatusCode(BcpFetcher.BcpResponse response) throws IOException;

}
