package no.difi.bcp.client.api;

/**
 * @author erlend
 */
public enum StatusCode {

    OK(false, null),
    NOT_FOUND(true, "No content found for the lookup."),
    ERROR(true, "Error in the service, try again later."),
    UNKNOWN(true, "Unknown error code (%s)");

    public final boolean error;

    public final String message;

    StatusCode(boolean error, String message) {
        this.error = error;
        this.message = message;
    }
}
