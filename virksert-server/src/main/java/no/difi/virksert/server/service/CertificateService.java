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

package no.difi.virksert.server.service;

import no.difi.certvalidator.Validator;
import no.difi.certvalidator.api.CertificateValidationException;
import no.difi.certvalidator.extra.NorwegianOrganizationNumberRule;
import no.difi.virksert.api.Role;
import no.difi.virksert.security.BusinessCertificateValidator;
import no.difi.virksert.server.domain.Certificate;
import no.difi.virksert.server.domain.CertificateRepository;
import no.difi.virksert.server.domain.Participant;
import no.difi.virksert.server.domain.Process;
import no.difi.virksert.server.form.UploadForm;
import no.difi.virksert.server.lang.NoCertificatesException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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

    @Transactional
    public void save(Certificate certificate) {
        certificate.setUpdated(System.currentTimeMillis());

        if (certificate.getIdentifier() == null)
            certificate.setIdentifier(UUID.randomUUID().toString());

        certificateRepository.save(certificate);
    }

    public int countActive(Participant participant) {
        return certificateRepository.countByParticipant(participant);
    }

    public List<Certificate> findByParticipantAndProcess(Participant participant, Process process, Role role)
            throws NoCertificatesException {
        List<Certificate> certificates = certificateRepository
                .findByParticipantAndProcessAndRole(participant, process, role);

        if (certificates.size() == 0)
            throw new NoCertificatesException("No certificates found.");

        return certificates;
    }
}
