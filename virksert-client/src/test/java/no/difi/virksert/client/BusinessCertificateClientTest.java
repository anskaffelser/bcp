package no.difi.virksert.client;

import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import no.difi.virksert.api.Mode;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;

/**
 * @author erlend
 */
public class BusinessCertificateClientTest {

    @Test(enabled = false)
    public void simple() throws Exception {
        BusinessCertificateClient client = BusinessCertificateClient
                .of(URI.create("http://localhost:8080/"), Mode.TEST);
        Assert.assertNotNull(client.fetch(ParticipantIdentifier.of("991825827"), ProcessIdentifier.of("some:process")));
    }
}
