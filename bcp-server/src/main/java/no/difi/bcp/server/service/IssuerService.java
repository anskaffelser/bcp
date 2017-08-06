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

package no.difi.bcp.server.service;

import net.klakegg.pkix.ocsp.CertificateResult;
import no.difi.bcp.server.domain.Issuer;
import no.difi.bcp.server.domain.IssuerRepository;
import no.difi.certvalidator.Validator;
import no.difi.certvalidator.api.Report;
import no.difi.certvalidator.rule.ChainRule;
import no.difi.certvalidator.rule.OCSPRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.UUID;

/**
 * @author erlend
 */
@Service
public class IssuerService {

    @Autowired
    private IssuerRepository issuerRepository;

    @Transactional(readOnly = true)
    public List<Issuer> findAll() {
        return issuerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Issuer get(String identifier) {
        return issuerRepository.findByIdentifier(identifier);
    }

    @Transactional
    public Issuer createOrFetch(Report report) throws CertificateEncodingException {
        X509Certificate issuedCertificate = report.get(Validator.CERTIFICATE);

        X509Certificate issuerCertificate = report.get(ChainRule.PATH).stream()
                .map(c -> (X509Certificate) c)
                .filter(c -> c.getSubjectX500Principal().getName()
                        .equals(issuedCertificate.getIssuerX500Principal().getName()))
                .findFirst()
                .orElseThrow(() -> new UncheckedIOException(new IOException("Unable to find issuer.")));

        Issuer issuer = issuerRepository.findBySerialNumberAndSubject(
                issuerCertificate.getSerialNumber().toString(),
                issuerCertificate.getSubjectX500Principal().getName());

        if (issuer == null) {
            issuer = new Issuer();
            issuer.setIdentifier(UUID.randomUUID().toString());
            issuer.setSerialNumber(issuerCertificate.getSerialNumber().toString());
            issuer.setSubject(issuerCertificate.getSubjectX500Principal().getName());
            issuer.setCertificate(issuerCertificate.getEncoded());

            CertificateResult certificateResult = report.get(OCSPRule.RESULT);

            if (certificateResult != null) {
                issuer.setNameHash(certificateResult.getIssuer().getIssuerNameHash());
                issuer.setKeyHash(certificateResult.getIssuer().getIssuerKeyHash());
            }

            issuerRepository.save(issuer);
        }

        return issuer;
    }

}
