package no.difi.bcp.client.api;

import no.difi.bcp.api.Role;
import no.difi.bcp.client.lang.BcpClientException;
import no.difi.bcp.client.model.ParticipantLookup;
import no.difi.bcp.client.model.ProcessLookup;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;

import java.io.InputStream;

/**
 * @author erlend
 */
public interface BcpVersion {

    String generatePath(ParticipantIdentifier participantIdentifier);

    String generatePath(ParticipantIdentifier participantIdentifier,
                        ProcessIdentifier processIdentifier,
                        Role role);

    ParticipantLookup parseParticipantLookup(InputStream inputStream) throws BcpClientException;

    ProcessLookup parseProcessLookup(InputStream inputStream) throws BcpClientException;

}
