package no.difi.virksert.server.lang;

/**
 * @author erlend
 */
public class InvalidInputException extends VirksertServerException {

    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }
}
