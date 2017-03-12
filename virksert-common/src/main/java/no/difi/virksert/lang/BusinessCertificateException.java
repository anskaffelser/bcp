package no.difi.virksert.lang;

/**
 * @author erlend
 */
public class BusinessCertificateException extends Exception {

    public BusinessCertificateException(String message) {
        super(message);
    }

    public BusinessCertificateException(String message, Throwable cause) {
        super(message, cause);
    }
}
