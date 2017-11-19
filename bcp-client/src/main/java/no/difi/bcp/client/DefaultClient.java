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
import no.difi.bcp.client.api.*;
import no.difi.bcp.client.fetcher.DefaultFetcher;
import no.difi.bcp.client.lang.BcpClientException;
import no.difi.bcp.client.model.Certificate;
import no.difi.bcp.client.model.ProcessLookup;
import no.difi.bcp.client.util.DomUtils;
import no.difi.bcp.security.BusinessCertificateValidator;
import no.difi.certvalidator.Validator;
import no.difi.certvalidator.api.CertificateValidationException;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import org.w3c.dom.Document;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.cert.X509Certificate;
import java.util.Objects;

/**
 * @author erlend
 */
class DefaultClient implements BcpClient {

    private BcpFetcher fetcher = new DefaultFetcher();

    private BcpVersion version;

    private BcpLocation location;

    private BusinessCertificateValidator validator;

    public DefaultClient(BcpFetcher fetcher, BcpVersion version, BcpLocation location,
                         BusinessCertificateValidator validator) {
        this.fetcher = fetcher;
        this.version = version;
        this.location = location;
        this.validator = validator;
    }

    @Override
    public X509Certificate fetchCertificate(ParticipantIdentifier participantIdentifier,
                                            ProcessIdentifier processIdentifier,
                                            Role role) throws BcpClientException {
        // Fetch URI for lookup
        URI serviceUri = location.forParticipantIdentifier(participantIdentifier);

        URI lookupUri = serviceUri.resolve(version.generatePath(participantIdentifier, processIdentifier, role));

        try (BcpFetcher.BcpResponse response = fetcher.fetch(lookupUri);
             InputStream inputStream = response.getContent();
             BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)) {

            // Parse status code
            StatusCode statusCode = version.parseStatusCode(response);

            if (statusCode.error)
                throw new BcpClientException(String.format("%s\r\nResponse: %s",
                        String.format(statusCode.message, response.getCode()),
                        new String(ByteStreams.toByteArray(bufferedInputStream))
                ));

            // Read document as DOM document
            Document document = DomUtils.parse(bufferedInputStream);

            ProcessLookup processLookup = version.parseProcessLookup(document);

            // Return first valid certificate
            return processLookup.getCertificates().stream()
                    .map(this::extractCert)
                    .filter(Objects::nonNull)
                    .filter(validator.getValidator()::isValid)
                    .findFirst()
                    .orElseThrow(() -> new BcpClientException("No valid certificate found."));
        } catch (IOException e) {
            throw new BcpClientException(e.getMessage(), e);
        }
    }

    private X509Certificate extractCert(Certificate certificate) {
        try {
            return Validator.getCertificate(certificate.getContent());
        } catch (CertificateValidationException e) {
            // Simply return null on invalid certificate data.
            return null;
        }
    }
}
