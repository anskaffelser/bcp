package no.difi.bcp.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;

/**
 * @author erlend
 */
@Configuration
public class PkiConfig {

    @Value("${bcp.keystore.path:}")
    private String keystorePath;

    @Value("${bcp.keystore.password:}")
    private String keystorePassword;

    @Value("${bcp.keystore.key.alias:}")
    private String keyAlias;

    @Value("${bcp.keystore.key.password:}")
    private String keyPassword;

    @Bean
    public KeyStore.PrivateKeyEntry getPrivateKeyEntry() throws Exception {
        if (keystorePath.isEmpty())
            return null;

        try (InputStream inputStream = Files.newInputStream(Paths.get(keystorePath))) {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(inputStream, keystorePassword.toCharArray());

            PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyAlias, keyPassword.toCharArray());
            Certificate certificate = keyStore.getCertificate(keyAlias);

            return new KeyStore.PrivateKeyEntry(privateKey, new Certificate[]{certificate});
        }
    }
}
