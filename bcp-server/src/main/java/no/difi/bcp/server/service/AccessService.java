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

package no.difi.bcp.server.service;

import no.difi.bcp.jaxb.registration.RegistrationType;
import no.difi.bcp.server.lang.BcpServerException;
import no.difi.bcp.server.lang.InvalidInputException;
import no.difi.certvalidator.Validator;
import no.difi.certvalidator.api.CertificateValidationException;
import no.difi.vefa.peppol.security.lang.PeppolSecurityException;
import no.difi.vefa.peppol.security.xmldsig.DomUtils;
import no.difi.vefa.peppol.security.xmldsig.XmldsigVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * @author erlend
 */
@Service
public class AccessService {

    private static final JAXBContext JAXB_CONTEXT;

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(RegistrationType.class);
        } catch (JAXBException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private Validator validator;

    public void update(InputStream inputStream) throws BcpServerException {
        try {
            // Prepare document for signature verification
            Document document = DomUtils.parse(inputStream);

            // Verify signature - receive certificate used for signing
            X509Certificate certificate = XmldsigVerifier.verify(document);

            // Validate certificate
            validator.validate(certificate);

            // Read data for updated access
            Unmarshaller unmarshaller = JAXB_CONTEXT.createUnmarshaller();
            RegistrationType registration = unmarshaller.unmarshal(new DOMSource(document), RegistrationType.class).getValue();

            // Fetch date
            if (registration.getTimestamp() == null)
                throw new InvalidInputException("Unable to find timestamp.");
            Date date = registration.getTimestamp().toGregorianCalendar().getTime();

            // TODO: Verify timestamp (age)

            // TODO: Fetch participant

            // TODO: Verify timestamp (participant)

            // TODO: Register certificate

            // TODO: Update access

        } catch (JAXBException | SAXException | ParserConfigurationException e) {
            throw new BcpServerException("Unable to parse content.", e);
        } catch (CertificateValidationException e) {
            throw new BcpServerException("Unable to verify certificate.", e);
        } catch (IOException | PeppolSecurityException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
