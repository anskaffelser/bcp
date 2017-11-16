package no.difi.bcp.client.version;

import no.difi.bcp.api.Role;
import no.difi.bcp.client.api.BcpVersion;
import no.difi.bcp.client.lang.BcpClientException;
import no.difi.bcp.client.model.Certificate;
import no.difi.bcp.client.model.ParticipantLookup;
import no.difi.bcp.client.model.ProcessLookup;
import no.difi.bcp.jaxb.v1.model.CertificateType;
import no.difi.bcp.jaxb.v1.model.IdentifierType;
import no.difi.bcp.jaxb.v1.model.ParticipantType;
import no.difi.bcp.jaxb.v1.model.ProcessType;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import no.difi.vefa.peppol.common.model.Scheme;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.util.stream.Collectors;

/**
 * Implementation of aspects specific to BCP version 1.
 *
 * @author erlend
 */
public class Version1 implements BcpVersion {

    public static final BcpVersion INSTANCE = new Version1();

    private static final JAXBContext JAXB_CONTEXT;

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(ParticipantType.class, ProcessType.class);
        } catch (JAXBException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    private Version1() {
        // No action.
    }

    @Override
    public String generatePath(ParticipantIdentifier participantIdentifier) {
        return String.format("api/v1/%s",
                participantIdentifier.urlencoded());
    }

    @Override
    public String generatePath(ParticipantIdentifier participantIdentifier,
                               ProcessIdentifier processIdentifier,
                               Role role) {
        return String.format("api/v1/%s/%s/%s",
                participantIdentifier.urlencoded(), processIdentifier.urlencoded(), role.name());
    }

    @Override
    public ParticipantLookup parseParticipantLookup(InputStream inputStream) throws BcpClientException {
        try {
            Unmarshaller unmarshaller = JAXB_CONTEXT.createUnmarshaller();

            ParticipantType participantType = unmarshaller.unmarshal(
                    new StreamSource(inputStream), ParticipantType.class).getValue();

            return ParticipantLookup.builder()
                    .participantIdentifier(parseParticipantIdentifier(participantType.getParticipantIdentifier()))
                    .processIdentifiers(participantType.getProcessReference().stream()
                            .map(this::parseProcessIdentifier)
                            .collect(Collectors.toList()))
                    .build();
        } catch (JAXBException e) {
            throw new BcpClientException("Unable to parse XML content.", e);
        }
    }

    @Override
    public ProcessLookup parseProcessLookup(InputStream inputStream) throws BcpClientException {
        try {
            Unmarshaller unmarshaller = JAXB_CONTEXT.createUnmarshaller();

            ProcessType processType = unmarshaller.unmarshal(
                    new StreamSource(inputStream), ProcessType.class).getValue();

            return ProcessLookup.builder()
                    .participantIdentifier(parseParticipantIdentifier(processType.getParticipantIdentifier()))
                    .processIdentifier(parseProcessIdentifier(processType.getProcessIdentifier()))
                    .certificates(processType.getCertificate().stream()
                            .map(this::parseCertificate)
                            .collect(Collectors.toList()))
                    .build();
        } catch (JAXBException e) {
            throw new BcpClientException("Unable to parse XML content.", e);
        }
    }

    private ParticipantIdentifier parseParticipantIdentifier(IdentifierType identifier) {
        return ParticipantIdentifier.of(identifier.getValue(), Scheme.of(identifier.getScheme()));
    }

    private ProcessIdentifier parseProcessIdentifier(IdentifierType identifier) {
        return ProcessIdentifier.of(identifier.getValue(), Scheme.of(identifier.getScheme()));
    }

    private Certificate parseCertificate(CertificateType certificateType) {
        return Certificate.builder()
                .serialNumber(certificateType.getSerialNumber())
                .expire(certificateType.getExpire())
                .content(certificateType.getValue())
                .build();
    }
}
