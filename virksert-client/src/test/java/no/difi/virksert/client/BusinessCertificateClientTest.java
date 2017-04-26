/*
 * Copyright 2017 Norwegian Agency for Public Management and eGovernment (Difi)
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package no.difi.virksert.client;

import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import no.difi.vefa.peppol.common.model.Scheme;
import no.difi.virksert.api.Mode;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;
import java.security.cert.X509Certificate;

/**
 * @author erlend
 */
public class BusinessCertificateClientTest {

    @Test(enabled = false)
    public void simple() throws Exception {
        BusinessCertificateClient client = BusinessCertificateClient
                .of(URI.create("http://localhost:8080/"), Mode.TEST);

        X509Certificate certificate = client.fetchCertificate(
                ParticipantIdentifier.of("9908:991825827"),
                ProcessIdentifier.of("urn:www.cenbii.eu:profile:bii01:ver2.0", Scheme.of("busdox-procid-ubl"))
        );

        Assert.assertNotNull(certificate);

        System.out.println(certificate.getSubjectX500Principal());
    }
}
