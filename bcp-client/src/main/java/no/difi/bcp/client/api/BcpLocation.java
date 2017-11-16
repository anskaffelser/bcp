package no.difi.bcp.client.api;

import no.difi.bcp.client.lang.BcpClientException;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;

import java.net.URI;

/**
 * @author erlend
 */
public interface BcpLocation {

    URI forParticipantIdentifier(ParticipantIdentifier participantIdentifier) throws BcpClientException;

}
