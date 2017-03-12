/*
 * Copyright 2017 Norwegian Agency for Public Management and eGovernment (Difi)
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package no.difi.virksert.client;

import com.google.common.io.ByteStreams;
import no.difi.certvalidator.api.CertificateValidationException;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import no.difi.virksert.api.Mode;
import no.difi.virksert.client.lang.VirksertClientException;
import no.difi.virksert.jaxb.v1.model.CertificateType;
import no.difi.virksert.jaxb.v1.model.ParticipantType;
import no.difi.virksert.jaxb.v1.model.ProcessType;
import no.difi.virksert.lang.BusinessCertificateException;
import no.difi.virksert.security.BusinessCertificateValidator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.security.cert.X509Certificate;

/**
 * @author erlend
 */
public class BusinessCertificateClient {

    private static JAXBContext jaxbContext;

    static {
        try {
            jaxbContext = JAXBContext.newInstance(ParticipantType.class, ProcessType.class);
        } catch (JAXBException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    private URI uri;

    private BusinessCertificateValidator validator;

    public static BusinessCertificateClient of(URI uri, Mode mode) throws BusinessCertificateException {
        return of(uri, (Enum<?>) mode);
    }

    public static BusinessCertificateClient of(URI uri, Enum<?> mode) throws BusinessCertificateException {
        return new BusinessCertificateClient(uri, BusinessCertificateValidator.of(mode));
    }

    public static BusinessCertificateClient of(URI uri, String mode) throws BusinessCertificateException {
        return new BusinessCertificateClient(uri, BusinessCertificateValidator.of(mode));
    }

    private BusinessCertificateClient(URI uri, BusinessCertificateValidator validator) {
        this.uri = uri;
        this.validator = validator;
    }

    public X509Certificate fetchCertificate(ParticipantIdentifier participantIdentifier,
                                            ProcessIdentifier processIdentifier)
            throws VirksertClientException {
        URI currentUri = uri.resolve(String.format("api/v1/%s/%s",
                participantIdentifier.urlencoded(), processIdentifier.getIdentifier()));

        try {
            HttpURLConnection connection = (HttpURLConnection) currentUri.toURL().openConnection();

            switch (connection.getResponseCode()) {
                case 400:
                case 404:
                case 500:
                    throw new VirksertClientException(new String(ByteStreams.toByteArray(connection.getInputStream())));
                case 200:
                    try (InputStream inputStream = connection.getInputStream();
                         BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)) {
                        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                        ProcessType processType = unmarshaller.unmarshal(
                                new StreamSource(bufferedInputStream), ProcessType.class).getValue();

                        for (CertificateType certificateType : processType.getCertificate()) {
                            try {
                                return validator.getValidator().validate(certificateType.getValue());
                            } catch (CertificateValidationException e) {
                                // No action...
                            }
                        }

                        throw new VirksertClientException("No valid certificate found.");
                    }
                default:
                    throw new VirksertClientException(String.format(
                            "Unknown error: %s", connection.getResponseMessage()));
            }
        } catch (Exception e) {
            throw new VirksertClientException(e.getMessage(), e);
        }
    }
}
