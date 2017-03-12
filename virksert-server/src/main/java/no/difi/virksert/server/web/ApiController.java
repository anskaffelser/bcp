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

package no.difi.virksert.server.web;

import no.difi.vefa.peppol.common.api.QualifiedIdentifier;
import no.difi.vefa.peppol.common.lang.PeppolParsingException;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import no.difi.virksert.jaxb.v1.model.*;
import no.difi.virksert.server.domain.Certificate;
import no.difi.virksert.server.domain.Participant;
import no.difi.virksert.server.domain.Process;
import no.difi.virksert.server.lang.*;
import no.difi.virksert.server.service.CertificateService;
import no.difi.virksert.server.service.ParticipantService;
import no.difi.virksert.server.service.ProcessService;
import no.difi.virksert.server.service.RegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    private static JAXBContext jaxbContext;

    static {
        try {
            jaxbContext = JAXBContext.newInstance(ParticipantType.class, ProcessType.class);
        } catch (JAXBException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private ProcessService processService;

    @Autowired
    private RegistrationService registrationService;

    @RequestMapping(
            value = "/{participantParam}",
            method = RequestMethod.GET)
    public void listSupportedProcesses(@PathVariable String participantParam, HttpServletResponse response)
            throws IOException, JAXBException, VirksertServerException {
        try {
            Participant participant = participantService.get(ParticipantIdentifier.parse(participantParam));

            ParticipantType participantType = new ParticipantType();
            participantType.setParticipantIdentifier(createIdentifier(participant.toVefa()));

            processService.findByParticipant(participant).stream()
                    .map(Process::toVefa)
                    .map(ApiController::createIdentifier)
                    .forEach(participantType.getProcessReference()::add);

            response.setContentType(MediaType.APPLICATION_XML_VALUE);

            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.marshal(OBJECT_FACTORY.createParticipant(participantType), response.getWriter());
        } catch (PeppolParsingException e) {
            throw new InvalidInputException(e.getMessage(), e);
        }
    }

    @RequestMapping(
            value = "/{participantParam}/{processParam:.+}",
            method = RequestMethod.GET)
    public void getCertificate(@PathVariable String participantParam, @PathVariable String processParam,
                               HttpServletResponse response) throws IOException, JAXBException, VirksertServerException {
        try {
            Participant participant = participantService.get(ParticipantIdentifier.parse(participantParam));
            Process process = processService.get(ProcessIdentifier.parse(processParam));

            ProcessType processType = new ProcessType();
            processType.setParticipantIdentifier(createIdentifier(participant.toVefa()));
            processType.setProcessIdentifier(createIdentifier(process.toVefa()));

            certificateService.findByParticipantAndProcess(participant, process).stream()
                    .map(ApiController::createCertificate)
                    .forEach(processType.getCertificate()::add);

            response.setContentType(MediaType.APPLICATION_XML_VALUE);

            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.marshal(OBJECT_FACTORY.createProcess(processType), response.getWriter());
        } catch (PeppolParsingException e) {
            throw new InvalidInputException(e.getMessage(), e);
        }
    }

    private static IdentifierType createIdentifier(QualifiedIdentifier identifier) {
        IdentifierType identifierType = new IdentifierType();
        identifierType.setScheme(identifier.getScheme().getValue());
        identifierType.setValue(identifier.getIdentifier());
        return identifierType;
    }

    private static CertificateType createCertificate(Certificate certificate) {
        CertificateType certificateType = new CertificateType();
        certificateType.setExpire(certificate.getExpiration());
        certificateType.setSerialNumber(certificate.getSerialNumber());
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

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class})
    public String handleRemainingExceptions(Exception e) {
        String identifier = UUID.randomUUID().toString();

        LOGGER.error("[{}] {}", identifier, e.getMessage(), e);

        return String.format("Internal server error. (code: %s)", identifier);
    }
}
