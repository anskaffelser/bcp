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

package no.difi.bcp.security;

import no.difi.bcp.api.Mode;
import no.difi.bcp.lang.BcpException;
import no.difi.certvalidator.Validator;
import no.difi.certvalidator.api.CertificateValidationException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.security.cert.X509Certificate;

/**
 * @author erlend
 */
public class BusinessCertificateValidatorTest {

    private BusinessCertificateValidator businessCertificateValidator;

    @BeforeClass
    public void beforeClass() throws CertificateValidationException, BcpException {
        businessCertificateValidator = BusinessCertificateValidator.of(Mode.TEST, null);
    }

    @Test
    public void simpleDifi() throws Exception {
        Assert.assertNotNull(businessCertificateValidator.getValidator());

        X509Certificate certificate = Validator.getCertificate(getClass().getResourceAsStream("/bc-test-difi.cer"));

        businessCertificateValidator.validate(certificate);
        businessCertificateValidator.validate(certificate.getEncoded());
        businessCertificateValidator.validate(getClass().getResourceAsStream("/bc-test-difi.cer"));
    }

    @Test(expectedExceptions = BcpException.class)
    public void receiptNotFound() throws Exception {
        BusinessCertificateValidator.of("/invalid-path.xml", null);
    }
}
