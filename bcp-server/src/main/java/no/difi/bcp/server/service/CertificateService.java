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

import no.difi.bcp.security.BusinessCertificateValidator;
import no.difi.bcp.server.domain.Certificate;
import no.difi.bcp.server.domain.CertificateRepository;
import no.difi.bcp.server.domain.Participant;
import no.difi.bcp.server.form.UploadForm;
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
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    public Page<Certificate> findAll(Participant participant, int page) {
        return certificateRepository.findByParticipant(participant, new PageRequest(page, 20));
    }

    @Transactional(readOnly = true)
    public List<Certificate> findAll(Participant participant) {
        return certificateRepository.findByParticipant(participant);
    }

    @Transactional(readOnly = true)
    public Certificate get(String identifier) {
        return certificateRepository.findByIdentifier(identifier);
    }

    public Certificate insert(Participant participant, UploadForm uploadForm)
            throws CertificateEncodingException, CertificateValidationException, IOException {
        return insert(participant, uploadForm.getFile().getInputStream());
    }

    @Transactional
    public Certificate insert(Participant participant, InputStream inputStream)
            throws CertificateEncodingException, CertificateValidationException {
        X509Certificate cert = Validator.getCertificate(inputStream);
        return insert(participant, cert);
    }

    @Transactional
    public Certificate insert(Participant participant, X509Certificate cert)
            throws CertificateEncodingException, CertificateValidationException {
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
}
