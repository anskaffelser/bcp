package no.difi.virksert.client;

import com.google.common.io.ByteStreams;
import no.difi.certvalidator.Validator;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import no.difi.virksert.api.Mode;
import no.difi.virksert.client.lang.VirksertClientException;
import no.difi.virksert.jaxb.v1.model.ParticipantType;
import no.difi.virksert.jaxb.v1.model.ProcessType;
import no.difi.virksert.lang.BusinessCertificateException;
import no.difi.virksert.security.BusinessCertificateValidator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.BufferedInputStream;
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

    public X509Certificate fetch(ParticipantIdentifier participantIdentifier, ProcessIdentifier processIdentifier)
            throws VirksertClientException {
        URI currentUri = uri.resolve(String.format("api/v1/%s/%s", participantIdentifier.urlencoded(), processIdentifier.getIdentifier()));

        try {
            HttpURLConnection connection = (HttpURLConnection) currentUri.toURL().openConnection();

            switch (connection.getResponseCode()) {
                case 400:
                case 404:
                case 500:
                    throw new VirksertClientException(new String(ByteStreams.toByteArray(connection.getInputStream())));
                case 200:
                    X509Certificate certificate = Validator.getCertificate(
                            new BufferedInputStream(connection.getInputStream()));

                    validator.validate(certificate);

                    return certificate;
                default:
                    throw new VirksertClientException(String.format(
                            "Unknown error: %s", connection.getResponseMessage()));
            }
        } catch (Exception e) {
            throw new VirksertClientException(e.getMessage(), e);
        }
    }
}
