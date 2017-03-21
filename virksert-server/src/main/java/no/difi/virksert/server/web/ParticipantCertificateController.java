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

import no.difi.certvalidator.api.CertificateValidationException;
import no.difi.vefa.peppol.common.lang.PeppolParsingException;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import no.difi.virksert.server.domain.Certificate;
import no.difi.virksert.server.domain.Participant;
import no.difi.virksert.server.domain.Process;
import no.difi.virksert.server.domain.Registration;
import no.difi.virksert.server.form.UploadForm;
import no.difi.virksert.server.lang.CertificateException;
import no.difi.virksert.server.lang.InvalidInputException;
import no.difi.virksert.server.lang.VirksertServerException;
import no.difi.virksert.server.service.CertificateService;
import no.difi.virksert.server.service.ParticipantService;
import no.difi.virksert.server.service.ProcessService;
import no.difi.virksert.server.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.security.cert.CertificateEncodingException;
import java.util.Base64;
import java.util.List;

/**
 * @author erlend
 */
@Controller
@RequestMapping("/participant/{participantParam}/certificate")
@PreAuthorize("hasAnyAuthority('ADMIN', #participantParam)")
public class ParticipantCertificateController {

    private static Base64.Encoder encoder = Base64.getEncoder();

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private ProcessService processService;

    @Autowired
    private RegistrationService registrationService;

    @RequestMapping(method = RequestMethod.GET)
    public String list(@PathVariable String participantParam, @RequestParam(defaultValue = "1") int page,
                       ModelMap modelMap) throws VirksertServerException {
        try {
            Participant participant = participantService.get(ParticipantIdentifier.parse(participantParam));

            modelMap.put("participant", participant);
            modelMap.put("list", certificateService.findAll(participant, page - 1));

            return "participant/certificate/list";
        } catch (PeppolParsingException e) {
            throw new InvalidInputException(e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String uploadForm(@PathVariable String participantParam, ModelMap modelMap) throws VirksertServerException {
        try {
            Participant participant = participantService.get(ParticipantIdentifier.parse(participantParam));

            modelMap.put("participant", participant);
            modelMap.put("form", new UploadForm());

            return "participant/certificate/upload";
        } catch (PeppolParsingException e) {
            throw new InvalidInputException(e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadSubmit(@PathVariable String participantParam, @ModelAttribute("form") UploadForm form,
                               RedirectAttributes redirectAttributes) throws IOException, VirksertServerException {
        try {
            Participant participant = participantService.get(ParticipantIdentifier.parse(participantParam));

            certificateService.insert(participant, form);

            // redirectAttributes.addFlashAttribute("message", "You successfully uploaded " + file.getOriginalFilename() + "!");

            return String.format("redirect:/participant/%s/certificate", participant.toVefa().toString());
        } catch (PeppolParsingException e) {
            throw new InvalidInputException(e.getMessage(), e);
        } catch (CertificateValidationException | CertificateEncodingException e) {
            throw new CertificateException(e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/upload/simple", method = RequestMethod.POST)
    public String uploadSimple(@PathVariable String participantParam, @RequestParam("file") MultipartFile file,
                               RedirectAttributes redirectAttributes) throws IOException, VirksertServerException {
        try {
            Participant participant = participantService.get(ParticipantIdentifier.parse(participantParam));

            certificateService.insert(participant, file.getInputStream(), "Uploaded");

            redirectAttributes.addFlashAttribute("message", "You successfully uploaded " + file.getOriginalFilename() + "!");

            return String.format("redirect:/participant/%s/certificate", participant.toVefa().toString());
        } catch (PeppolParsingException e) {
            throw new InvalidInputException(e.getMessage(), e);
        } catch (CertificateValidationException | CertificateEncodingException e) {
            throw new CertificateException(e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.GET)
    public String view(@PathVariable String participantParam, @PathVariable String identifier, ModelMap modelMap)
            throws VirksertServerException {
        try {
            Participant participant = participantService.get(ParticipantIdentifier.parse(participantParam));
            Certificate certificate = certificateService.get(participant, identifier);
            List<Process> processesConnected = processService.findByCertificate(certificate);
            List<Process> processesAvailable = processService.findAll();
            processesAvailable.removeAll(processesConnected);

            modelMap.put("participant", participant);
            modelMap.put("certificate", certificate);
            modelMap.put("processesConnected", processesConnected);
            modelMap.put("processesAvailable", processesAvailable);

            return "participant/certificate/view";
        } catch (PeppolParsingException e) {
            throw new InvalidInputException(e.getMessage(), e);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/{identifier}/download", method = RequestMethod.GET)
    public void view(@PathVariable String participantParam, @PathVariable String identifier,
                     HttpServletResponse response) throws IOException, VirksertServerException {
        try {
            Participant participant = participantService.get(ParticipantIdentifier.parse(participantParam));
            Certificate certificate = certificateService.get(participant, identifier);

            response.setContentType("application/pkix-cert");
            response.setHeader("Content-Disposition", String.format(
                    "attachment; filename=%s-%s.crt", participant.getIdentifier(), certificate.getIdentifier()));

            Writer writer = response.getWriter();
            writer.write("-----BEGIN CERTIFICATE-----\r\n");
            writer.write(encoder.encodeToString(certificate.getCertificate()));
            writer.write("\r\n-----END CERTIFICATE-----");
            writer.flush();
        } catch (PeppolParsingException e) {
            throw new InvalidInputException(e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/{identifier}/connect", method = RequestMethod.POST)
    public String connectProcess(@PathVariable String participantParam, @PathVariable String identifier,
                     @RequestParam("process") String processParam) throws IOException, VirksertServerException {
        try {
            Participant participant = participantService.get(ParticipantIdentifier.parse(participantParam));
            Process process = processService.get(ProcessIdentifier.parse(processParam));
            Certificate certificate = certificateService.get(participant, identifier);

            Registration registration = new Registration();
            registration.setParticipant(participant);
            registration.setProcess(process);
            registration.setCertificate(certificate);
            registrationService.save(registration);

            return String.format("redirect:/participant/%s/certificate/%s", participantParam, identifier);
        } catch (PeppolParsingException e) {
            throw new InvalidInputException(e.getMessage(), e);
        }
    }
}
