package no.difi.bcp.client.fetcher;

import no.difi.bcp.client.api.BcpFetcher;
import no.difi.bcp.client.lang.BcpClientException;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author erlend
 */
public class CountingFetcher implements BcpFetcher {

    private final BcpFetcher fetcher;

    private final AtomicLong counter;

    public static BcpFetcher with(BcpFetcher fetcher) {
        return new CountingFetcher(fetcher);
    }

    private CountingFetcher(BcpFetcher fetcher) {
        this.fetcher = fetcher;
        this.counter = new AtomicLong(0);
    }

    @Override
    public BcpResponse fetch(URI uri) throws IOException, BcpClientException {
        counter.incrementAndGet();
        return fetcher.fetch(uri);
    }

    public long count() {
        return counter.get();
    }
}
