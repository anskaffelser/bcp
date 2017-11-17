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

package no.difi.bcp.server.web;

import com.google.common.io.ByteStreams;
import no.difi.bcp.api.Role;
import no.difi.bcp.jaxb.v1.model.*;
import no.difi.bcp.security.BusinessCertificateValidator;
import no.difi.bcp.server.domain.Certificate;
import no.difi.bcp.server.domain.Participant;
import no.difi.bcp.server.domain.Process;
import no.difi.bcp.server.domain.Registration;
import no.difi.bcp.server.lang.*;
import no.difi.bcp.server.service.ParticipantService;
import no.difi.bcp.server.service.ProcessService;
import no.difi.bcp.server.service.RegistrationService;
import no.difi.bcp.server.service.SignerService;
import no.difi.certvalidator.ValidatorGroup;
import no.difi.vefa.peppol.common.api.QualifiedIdentifier;
import no.difi.vefa.peppol.common.lang.PeppolParsingException;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import no.difi.vefa.peppol.security.xmldsig.DomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.util.UUID;

/**
 * @author erlend
 */
@RestController
@RequestMapping("/api/v1")
public class ApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiController.class);

    private static final JAXBContext JAXB_CONTEXT;

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    @Value("${bcp.api.v1:0}")
    private int revision;

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(ParticipantType.class, ProcessType.class);
        } catch (JAXBException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private ProcessService processService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private SignerService signerService;

    @Autowired
    private ValidatorGroup validatorGroup;

    @Autowired
    private BusinessCertificateValidator businessCertificateValidator;

    private ValidatorType validatorType;

    @PostConstruct
    public void postConstruct() {
        validatorType = new ValidatorType();
        validatorType.setName(validatorGroup.getName());
        validatorType.setVersion(validatorGroup.getVersion());
    }

    @RequestMapping(
            value = "/{participantParam}",
            method = RequestMethod.GET)
    public void listSupportedProcesses(@PathVariable String participantParam, HttpServletResponse response)
            throws IOException, JAXBException, BcpServerException {
        try {
            Participant participant = participantService.get(ParticipantIdentifier.parse(participantParam));

            ParticipantType participantType = new ParticipantType();
            participantType.setParticipantIdentifier(createIdentifier(participant.toVefa()));

            registrationService.findProcesses(participant).stream()
                    .map(this::createIdentifier)
                    .forEach(participantType.getProcessReference()::add);

            response.setContentType(MediaType.APPLICATION_XML_VALUE);

            Marshaller marshaller = JAXB_CONTEXT.createMarshaller();

            if (signerService.active()) {
                Document document = DomUtils.newDocumentBuilder().newDocument();
                marshaller.marshal(OBJECT_FACTORY.createParticipant(participantType), document);
                signerService.sign(document, response.getOutputStream());
            } else {
                marshaller.marshal(OBJECT_FACTORY.createParticipant(participantType), response.getWriter());
            }
        } catch (PeppolParsingException e) {
            throw new InvalidInputException(e.getMessage(), e);
        }
    }

    @RequestMapping(
            value = "/{participantParam}/{processParam:.+}",
            method = RequestMethod.GET)
    public void getCertificate(@PathVariable String participantParam, @PathVariable String processParam,
                               HttpServletResponse response) throws IOException, JAXBException, BcpServerException {
        getCertificate(participantParam, processParam, Role.REQUEST, response);
    }

    @RequestMapping(
            value = "/{participantParam}/{processParam}/{role}",
            method = RequestMethod.GET)
    public void getCertificate(@PathVariable String participantParam, @PathVariable String processParam,
                               @PathVariable Role role, HttpServletResponse response)
            throws IOException, JAXBException, BcpServerException {
        try {
            Participant participant = participantService.get(ParticipantIdentifier.parse(participantParam));
            Process process = processService.get(ProcessIdentifier.parse(processParam));

            ProcessType processType = new ProcessType();
            processType.setParticipantIdentifier(createIdentifier(participant.toVefa()));
            processType.setProcessIdentifier(createIdentifier(process.toVefa()));

            if (revision >= 1) {
                processType.setRole(RoleType.valueOf(role.name()));
                processType.setValidator(validatorType);
            }

            registrationService.findCertificates(participant, process, role).stream()
                    .map(this::createCertificate)
                    .forEach(processType.getCertificate()::add);

            response.setContentType(MediaType.APPLICATION_XML_VALUE);

            Marshaller marshaller = JAXB_CONTEXT.createMarshaller();

            if (signerService.active()) {
                Document document = DomUtils.newDocumentBuilder().newDocument();
                marshaller.marshal(OBJECT_FACTORY.createProcess(processType), document);
                signerService.sign(document, response.getOutputStream());
            } else {
                marshaller.marshal(OBJECT_FACTORY.createProcess(processType), response.getWriter());
            }
        } catch (PeppolParsingException e) {
            throw new InvalidInputException(e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/validator", method = RequestMethod.GET)
    public void getValidator(HttpServletResponse response) throws IOException, BcpServerException, SAXException {
        response.setContentType(MediaType.APPLICATION_XML_VALUE);

        if (signerService.active()) {
            Document document = DomUtils.newDocumentBuilder().parse(businessCertificateValidator.getValidatorSource());
            signerService.sign(document, response.getOutputStream());
        } else {
            ByteStreams.copy(businessCertificateValidator.getValidatorSource(), response.getOutputStream());
        }
    }

    private static IdentifierType createIdentifier(QualifiedIdentifier identifier) {
        IdentifierType identifierType = new IdentifierType();
        identifierType.setScheme(identifier.getScheme().getValue());
        identifierType.setValue(identifier.getIdentifier());
        return identifierType;
    }

    private ProcessReferenceType createIdentifier(Registration registration) {
        ProcessReferenceType processReferenceType = new ProcessReferenceType();
        processReferenceType.setScheme(registration.getProcess().getScheme());
        processReferenceType.setValue(registration.getProcess().getIdentifier());
        processReferenceType.setRole(RoleType.fromValue(registration.getRole().toString()));
        return processReferenceType;
    }

    private CertificateType createCertificate(Certificate certificate) {
        CertificateType certificateType = new CertificateType();
        certificateType.setExpire(certificate.getExpiration());
        certificateType.setSerialNumber(certificate.getSerialNumber());
        if (revision >= 1)
            certificateType.setLastValidation(certificate.getValidated());
        certificateType.setValue(certificate.getCertificate());
        return certificateType;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({ParticipantNotFoundException.class, ProcessNotFoundException.class,
            NoCertificatesException.class})
    public String handleNotFoundException(Exception e) {
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({InvalidInputException.class})
    public String handleInvalidInputException(Exception e) {
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public String handleConverterException(Exception e) {
        return "Invalid input.";
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class})
    public String handleRemainingExceptions(Exception e) {
        String identifier = UUID.randomUUID().toString();

        LOGGER.error("[{}] {}", identifier, e.getMessage(), e);

        return String.format("Internal server error. (code: %s)", identifier);
    }
}
