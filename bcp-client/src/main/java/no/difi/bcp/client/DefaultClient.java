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

import com.google.common.io.ByteStreams;
import no.difi.bcp.api.Role;
import no.difi.bcp.client.api.BcpClient;
import no.difi.bcp.client.api.BcpLocation;
import no.difi.bcp.client.api.BcpVersion;
import no.difi.bcp.client.lang.BcpClientException;
import no.difi.bcp.client.model.Certificate;
import no.difi.bcp.client.model.ProcessLookup;
import no.difi.bcp.security.BusinessCertificateValidator;
import no.difi.certvalidator.api.CertificateValidationException;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.security.cert.X509Certificate;

/**
 * @author erlend
 */
class DefaultClient implements BcpClient {

    private BcpVersion version;

    private BcpLocation location;

    private BusinessCertificateValidator validator;

    public DefaultClient(BcpVersion version, BcpLocation location, BusinessCertificateValidator validator) {
        this.version = version;
        this.location = location;
        this.validator = validator;
    }

    @Override
    public X509Certificate fetchCertificate(ParticipantIdentifier participantIdentifier,
                                            ProcessIdentifier processIdentifier,
                                            Role role) throws BcpClientException {
        // Fetch URI for BCP
        URI rootUri = location.forParticipantIdentifier(participantIdentifier);

        // Generate URI for lookup
        URI currentUri = rootUri.resolve(version.generatePath(participantIdentifier, processIdentifier, role));

        try {
            HttpURLConnection connection = (HttpURLConnection) currentUri.toURL().openConnection();

            switch (connection.getResponseCode()) {
                case 400:
                case 404:
                case 500:
                    throw new BcpClientException(new String(ByteStreams.toByteArray(connection.getInputStream())));
                case 200:
                    try (InputStream inputStream = connection.getInputStream();
                         BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)) {

                        ProcessLookup processLookup = version.parseProcessLookup(bufferedInputStream);

                        for (Certificate certificate : processLookup.getCertificates()) {
                            try {
                                return validator.getValidator().validate(certificate.getContent());
                            } catch (CertificateValidationException e) {
                                // Certificates not deemed valid are to be silently discarded.
                            }
                        }

                        throw new BcpClientException("No valid certificate found.");
                    }
                default:
                    throw new BcpClientException(String.format(
                            "Unknown error: %s", connection.getResponseMessage()));
            }
        } catch (Exception e) {
            throw new BcpClientException(e.getMessage(), e);
        }
    }
}
