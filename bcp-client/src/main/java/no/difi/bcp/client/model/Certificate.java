package no.difi.bcp.client.model;

import lombok.Builder;
import lombok.Getter;

/**
 * @author erlend
 */
@Builder
@Getter
public class Certificate {

    private String serialNumber;

    private long expire;

    private byte[] content;

}
