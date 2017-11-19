package no.difi.bcp.client.util;

import no.difi.bcp.client.lang.BcpClientException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author erlend
 */
public class DomUtils {

    public static final DocumentBuilderFactory documentBuilderFactory;

    static {
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
    }

    public static Document parse(InputStream inputStream)
            throws IOException, BcpClientException {
        try {
            return documentBuilderFactory.newDocumentBuilder().parse(inputStream);
        } catch (SAXException | ParserConfigurationException e) {
            throw new BcpClientException("Unable to parse XML content.", e);
        }
    }
}