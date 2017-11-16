package no.difi.bcp.client.location;

import no.difi.bcp.client.api.BcpLocation;
import no.difi.bcp.client.lang.BcpClientException;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;

import java.net.URI;

/**
 * @author erlend
 */
public class StaticLocation implements BcpLocation {

    private URI uri;

    public static BcpLocation of(URI uri) {
        return new StaticLocation(uri);
    }

    private StaticLocation(URI uri) {
        this.uri = uri;
    }

    @Override
    public URI forParticipantIdentifier(ParticipantIdentifier participantIdentifier) throws BcpClientException {
        return uri;
    }
}
