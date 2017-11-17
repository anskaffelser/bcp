package no.difi.bcp.server.service;

import no.difi.bcp.server.lang.SigningException;
import no.difi.vefa.peppol.security.lang.PeppolSecurityException;
import no.difi.vefa.peppol.security.xmldsig.XmldsigSigner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.security.KeyStore;

/**
 * @author erlend
 */
@Service
public class SignerService {

    private XmldsigSigner xmldsigSigner = XmldsigSigner.SHA256();

    @Autowired(required = false)
    private KeyStore.PrivateKeyEntry privateKeyEntry;

    public boolean active() {
        return privateKeyEntry != null;
    }

    public void sign(Document document, OutputStream outputStream) throws SigningException {
        try {
            xmldsigSigner.sign(document, privateKeyEntry, new StreamResult(outputStream));
        } catch (PeppolSecurityException e) {
            throw new SigningException(e.getMessage(), e);
        }
    }
}
