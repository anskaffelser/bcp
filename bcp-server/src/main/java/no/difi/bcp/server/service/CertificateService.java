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

package no.difi.bcp.server.service;

import no.difi.bcp.api.Role;
import no.difi.bcp.security.BusinessCertificateValidator;
import no.difi.bcp.server.domain.Certificate;
import no.difi.bcp.server.domain.CertificateRepository;
import no.difi.bcp.server.domain.Participant;
import no.difi.bcp.server.domain.Process;
import no.difi.bcp.server.form.UploadForm;
import no.difi.bcp.server.lang.NoCertificatesException;
import no.difi.certvalidator.Validator;
import no.difi.certvalidator.api.CertificateValidationException;
import no.difi.certvalidator.extra.NorwegianOrganizationNumberRule;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Collections;
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

    public List<Certificate> findAll(Participant participant) {
        return certificateRepository.findByParticipant(participant);
    }

    public Certificate get(String identifier) {
        return certificateRepository.findByIdentifier(identifier);
    }

    public Certificate insert(Participant participant, UploadForm uploadForm)
            throws CertificateEncodingException, CertificateValidationException, IOException {
        return insert(participant, uploadForm.getFile().getInputStream());
    }

    public Certificate insert(Participant participant, InputStream inputStream)
            throws CertificateEncodingException, CertificateValidationException {
        X509Certificate cert = Validator.getCertificate(inputStream);
        validator.validate(cert);

        new NorwegianOrganizationNumberRule(s -> String.format("9908:%s", s).equals(participant.getIdentifier()))
                .validate(cert);

        X500Name x500name = new JcaX509CertificateHolder(cert).getSubject();
        String name = IETFUtils.valueToString(x500name.getRDNs(BCStyle.O)[0].getFirst().getValue());

        if (x500name.getRDNs(BCStyle.OU).length > 0)
            name = String.format("%s - %s", name, IETFUtils.valueToString(x500name.getRDNs(BCStyle.OU)[0].getFirst().getValue()));

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

    public List<Certificate> findByParticipantAndProcess(Participant participant, Process process, Role role)
            throws NoCertificatesException {
        List<Certificate> certificates = Collections.emptyList(); // TODO

        if (certificates.size() == 0)
            throw new NoCertificatesException("No certificates found.");

        return certificates;
    }
}
