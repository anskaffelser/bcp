/*
 *  Copyright 2017 Norwegian Agency for Public Management and eGovernment (Difi)
 *
 *  Licensed under the EUPL, Version 1.1 or – as soon they
 *  will be approved by the European Commission - subsequent
 *  versions of the EUPL (the "Licence");
 *
 *  You may not use this work except in compliance with the Licence.
 *
 *  You may obtain a copy of the Licence at:
 *
 *  https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 *  Unless required by applicable law or agreed to in
 *  writing, software distributed under the Licence is
 *  distributed on an "AS IS" basis,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *  express or implied.
 *  See the Licence for the specific language governing
 *  permissions and limitations under the Licence.
 */

package no.difi.bcp.client;

import no.difi.bcp.api.Mode;
import no.difi.bcp.api.Role;
import no.difi.bcp.client.api.BcpClient;
import no.difi.bcp.client.lang.BcpClientException;
import no.difi.bcp.lang.BcpException;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;

import java.net.URI;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * @author erlend
 *
 * This class is deprecated. Please use BcpClientBuilder. Example:
 *
 * BcpClient bcpClient = BcpClientBuilder.newInstance()
 *                          .location("http://bcp.com/")
 *                          .validator(Mode.PRODUCTION)
 *                          .build();
 */
@Deprecated
public class BusinessCertificateClient implements BcpClient {

    private BcpClient client;

    public static BusinessCertificateClient of(URI uri, Mode mode) throws BcpException {
        return of(uri, mode, null);
    }

    public static BusinessCertificateClient of(URI uri, Mode mode, Map<String, Object> values) throws BcpException {
        return of(uri, (Enum<?>) mode, values);
    }

    public static BusinessCertificateClient of(URI uri, Enum<?> mode) throws BcpException {
        return of(uri, mode, null);
    }

    public static BusinessCertificateClient of(URI uri, Enum<?> mode, Map<String, Object> values) throws BcpException {
        return new BusinessCertificateClient(
                BcpClientBuilder.newInstance()
                        .location(uri)
                        .validator(mode, values)
                        .build()
        );
    }

    public static BusinessCertificateClient of(URI uri, String mode) throws BcpException {
        return of(uri, mode, null);
    }

    public static BusinessCertificateClient of(URI uri, String mode, Map<String, Object> values) throws BcpException {
        return new BusinessCertificateClient(
                BcpClientBuilder.newInstance()
                        .location(uri)
                        .validator(mode, values)
                        .build()
        );
    }

    private BusinessCertificateClient(BcpClient client) {
        this.client = client;
    }

    @Override
    public X509Certificate fetchCertificate(ParticipantIdentifier participantIdentifier,
                                            ProcessIdentifier processIdentifier,
                                            Role role) throws BcpClientException {
        return client.fetchCertificate(participantIdentifier, processIdentifier, role);
    }
}
