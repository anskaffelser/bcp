package no.difi.virksert.client.lang;

import no.difi.virksert.lang.BusinessCertificateException;

/**
 * @author erlend
 */
public class VirksertClientException extends BusinessCertificateException {

    public VirksertClientException(String message) {
        super(message);
    }

    public VirksertClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
