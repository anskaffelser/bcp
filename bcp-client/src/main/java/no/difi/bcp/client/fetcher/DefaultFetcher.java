package no.difi.bcp.client.fetcher;

import no.difi.bcp.client.api.BcpFetcher;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * The default implementation of fetcher using the HTTP client provided by Java.
 *
 * @author erlend
 */
public class DefaultFetcher implements BcpFetcher {

    @Override
    public BcpResponse fetch(URI uri) throws IOException {
        return new DefaultResponse(uri, (HttpURLConnection) uri.toURL().openConnection());
    }

    private class DefaultResponse implements BcpResponse {

        private final URI uri;

        private final HttpURLConnection connection;

        public DefaultResponse(URI uri, HttpURLConnection connection) {
            this.uri = uri;
            this.connection = connection;
        }

        @Override
        public URI getUri() {
            return uri;
        }

        @Override
        public int getCode() throws IOException {
            return connection.getResponseCode();
        }

        @Override
        public InputStream getContent() throws IOException {
            return connection.getInputStream();
        }

        @Override
        public void close() throws IOException {
            // No action.
        }
    }
}
