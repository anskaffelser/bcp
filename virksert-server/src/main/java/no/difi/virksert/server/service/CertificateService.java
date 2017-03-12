package no.difi.virksert.server.service;

import no.difi.certvalidator.Validator;
import no.difi.certvalidator.api.CertificateValidationException;
import no.difi.certvalidator.extra.NorwegianOrganizationNumberRule;
import no.difi.virksert.security.BusinessCertificateValidator;
import no.difi.virksert.server.domain.Certificate;
import no.difi.virksert.server.domain.CertificateRepository;
import no.difi.virksert.server.domain.Participant;
import no.difi.virksert.server.domain.Process;
import no.difi.virksert.server.form.UploadForm;
import no.difi.virksert.server.lang.NoCertificatesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.UUID;

/**
 * @author erlend
 */
@Service
public class CertificateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertificateService.class);

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private BusinessCertificateValidator validator;

    public Page<Certificate> findAll(Participant participant, int page) {
        return certificateRepository.findByParticipant(participant, new PageRequest(page, 20));
    }

    public Certificate get(Participant participant, String identifier) {
        return certificateRepository.findByParticipantAndIdentifier(participant, identifier);
    }

    public Certificate insert(Participant participant, UploadForm uploadForm)
            throws CertificateEncodingException, CertificateValidationException, IOException {
        return insert(participant, uploadForm.getFile().getInputStream(), uploadForm.getName());
    }

    public Certificate insert(Participant participant, InputStream inputStream, String name)
            throws CertificateEncodingException, CertificateValidationException {
        X509Certificate cert = Validator.getCertificate(inputStream);
        validator.validate(cert);

        new NorwegianOrganizationNumberRule(s -> String.format("9908:%s", s).equals(participant.getIdentifier()))
                .validate(cert);

        Certificate certificate = new Certificate();
        certificate.setCertificate(cert.getEncoded());
        certificate.setSerialNumber(cert.getSerialNumber().toString());
        certificate.setExpiration(cert.getNotAfter().getTime());
        certificate.setParticipant(participant);
        certificate.setName(name);
        certificate.setSubject(cert.getSubjectX500Principal().getName());
        certificate.setIssuer(cert.getIssuerX500Principal().getName());
        save(certificate);

        return certificate;
    }

    public void save(Certificate certificate) {
        certificate.setUpdated(System.currentTimeMillis());

        if (certificate.getIdentifier() == null)
            certificate.setIdentifier(UUID.randomUUID().toString());

        certificateRepository.save(certificate);
    }

    public int countActive(Participant participant) {
        return certificateRepository.countByParticipant(participant);
    }

    public List<Certificate> findByParticipantAndProcess(Participant participant, Process process)
            throws NoCertificatesException {
        List<Certificate> certificates = certificateRepository.findByParticipantAndProcess(participant, process);

        if (certificates.size() == 0)
            throw new NoCertificatesException("No certificates found.");

        return certificates;
    }
}
