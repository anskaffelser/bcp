package no.difi.virksert.server.lang;

/**
 * @author erlend
 */
public class CertificateException extends VirksertServerException {

    public CertificateException(String message) {
        super(message);
    }

    public CertificateException(String message, Throwable cause) {
        super(message, cause);
    }
}
