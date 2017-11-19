package no.difi.bcp.client.api;

import no.difi.bcp.client.lang.BcpClientException;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * @author erlend
 */
public interface BcpFetcher {

    BcpResponse fetch(URI uri) throws IOException, BcpClientException;

    interface BcpResponse extends Closeable {

        URI getUri() throws IOException;

        int getCode() throws IOException;

        InputStream getContent() throws IOException;

    }

}
