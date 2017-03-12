package no.difi.virksert.security;

import no.difi.certvalidator.Validator;
import no.difi.certvalidator.api.CertificateValidationException;
import no.difi.virksert.api.Mode;
import no.difi.virksert.lang.BusinessCertificateException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.security.cert.X509Certificate;

/**
 * @author erlend
 */
public class BusinessCertificateValidatorTest {

    private X509Certificate certificate;

    private BusinessCertificateValidator businessCertificateValidator;

    @BeforeClass
    public void beforeClass() throws CertificateValidationException, BusinessCertificateException {
        certificate = Validator.getCertificate(getClass().getResourceAsStream("/bc-test-difi.cer"));
        businessCertificateValidator = BusinessCertificateValidator.of(Mode.TEST);
    }

    @Test
    public void simpleTest() throws Exception {
        Assert.assertNotNull(businessCertificateValidator.getValidator());

        businessCertificateValidator.validate(certificate);
        businessCertificateValidator.validate(certificate.getEncoded());
        businessCertificateValidator.validate(getClass().getResourceAsStream("/bc-test-difi.cer"));
    }

    @Test(expectedExceptions = BusinessCertificateException.class)
    public void receiptNotFound() throws Exception {
        BusinessCertificateValidator.of("/invalid-path.xml");
    }
}
