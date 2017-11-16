/*
 *  Copyright 2017 Norwegian Agency for Public Management and eGovernment (Difi)
 *
 *  Licensed under the EUPL, Version 1.1 or – as soon they
 *  will be approved by the European Commission - subsequent
 *  versions of the EUPL (the "Licence");
 *
 *  You may not use this work except in compliance with the Licence.
 *
 *  You may obtain a copy of the Licence at:
 *
 *  https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 *  Unless required by applicable law or agreed to in
 *  writing, software distributed under the Licence is
 *  distributed on an "AS IS" basis,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *  express or implied.
 *  See the Licence for the specific language governing
 *  permissions and limitations under the Licence.
 */

package no.difi.bcp.client;

import no.difi.bcp.api.Mode;
import no.difi.bcp.api.Role;
import no.difi.bcp.client.api.BcpClient;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import no.difi.vefa.peppol.common.model.Scheme;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.security.cert.X509Certificate;

/**
 * @author erlend
 */
public class DefaultClientTest {

    @Test // (enabled = false)
    public void simple() throws Exception {
        BcpClient client = BcpClientBuilder.newInstance()
                .location("https://test-bcp.difi.blufo.net/")
                .validator(Mode.TEST)
                .build();

        X509Certificate certificate = client.fetchCertificate(
                ParticipantIdentifier.of("9908:984851006"),
                ProcessIdentifier.of("urn:fdc:bits.no:2017:profile:01:1.0", Scheme.of("busdox-procid-ubl")),
                Role.REQUEST
        );

        Assert.assertNotNull(certificate);

        System.out.println(certificate.getSubjectX500Principal());
    }
}
