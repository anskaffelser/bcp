package no.difi.bcp.client;

import no.difi.bcp.api.Mode;
import no.difi.bcp.client.api.BcpClient;
import no.difi.bcp.client.api.BcpLocation;
import no.difi.bcp.client.api.BcpVersion;
import no.difi.bcp.client.lang.BcpClientException;
import no.difi.bcp.client.location.StaticLocation;
import no.difi.bcp.client.version.Version1;
import no.difi.bcp.lang.BcpException;
import no.difi.bcp.security.BusinessCertificateValidator;

import java.net.URI;
import java.util.Map;

/**
 * @author erlend
 */
public class BcpClientBuilder {

    private BcpLocation location;

    private BusinessCertificateValidator validator;

    private BcpVersion version;

    public static BcpClientBuilder newInstance() {
        return new BcpClientBuilder();
    }

    // Location

    public BcpClientBuilder location(String uri) {
        return location(URI.create(uri));
    }

    public BcpClientBuilder location(URI uri) {
        return location(StaticLocation.of(uri));
    }

    public BcpClientBuilder location(BcpLocation location) {
        this.location = location;
        return this;
    }

    // Validator

    public BcpClientBuilder validator(Mode mode) throws BcpClientException {
        return validator(mode, null);
    }

    public BcpClientBuilder validator(Mode mode, Map<String, Object> values) throws BcpClientException {
        return validator((Enum<?>) mode, values);
    }

    public BcpClientBuilder validator(Enum<?> mode) throws BcpClientException {
        return validator(mode, null);
    }

    public BcpClientBuilder validator(Enum<?> mode, Map<String, Object> values) throws BcpClientException {
        try {
            this.validator = BusinessCertificateValidator.of(mode, values);
            return this;
        } catch (BcpException e) {
            throw new BcpClientException(e.getMessage(), e);
        }
    }

    public BcpClientBuilder validator(String mode) throws BcpClientException {
        return validator(mode, null);
    }

    public BcpClientBuilder validator(String mode, Map<String, Object> values) throws BcpClientException {
        try {
            this.validator = BusinessCertificateValidator.of(mode, values);
            return this;
        } catch (BcpException e) {
            throw new BcpClientException(e.getMessage(), e);
        }
    }

    // Version

    public BcpClientBuilder version(BcpVersion version) {
        this.version = version;
        return this;
    }

    // Build!

    public BcpClient build() {
        if (location == null)
            throw new IllegalStateException("Location is not set.");

        if (validator == null)
            throw new IllegalStateException("Validator is not set.");

        if (version == null)
            version = Version1.INSTANCE;

        return new DefaultClient(version, location, validator);
    }
}
