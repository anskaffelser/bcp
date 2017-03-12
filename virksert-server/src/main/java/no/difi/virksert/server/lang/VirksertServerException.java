package no.difi.virksert.server.lang;

/**
 * @author erlend
 */
public class VirksertServerException extends Exception {

    public VirksertServerException(String message) {
        super(message);
    }

    public VirksertServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
