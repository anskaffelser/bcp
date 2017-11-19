package no.difi.bcp.client.api;

import no.difi.bcp.api.Role;
import no.difi.bcp.client.lang.BcpClientException;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.security.cert.X509Certificate;
import java.util.List;

/**
 * @author erlend
 */
public interface BcpClient {

    default X509Certificate fetchCertificate(ParticipantIdentifier participantIdentifier,
                                             ProcessIdentifier processIdentifier) throws BcpClientException {
        return fetchCertificate(participantIdentifier, processIdentifier, Role.REQUEST);
    }

    X509Certificate fetchCertificate(ParticipantIdentifier participantIdentifier,
                                     ProcessIdentifier processIdentifier,
                                     Role role) throws BcpClientException;

    default List<ProcessIdentifier> fetchProcesses(ParticipantIdentifier participantIdentifier)
            throws BcpClientException {
        throw new NotImplementedException();
    }
}
