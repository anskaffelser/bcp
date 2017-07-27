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

import no.difi.bcp.server.domain.*;
import no.difi.bcp.server.domain.Process;
import no.difi.bcp.server.form.CertificateForm;
import no.difi.bcp.server.form.UploadForm;
import no.difi.bcp.server.lang.BcpServerException;
import no.difi.bcp.server.lang.CertificateException;
import no.difi.bcp.server.lang.InvalidInputException;
import no.difi.bcp.server.service.CertificateService;
import no.difi.bcp.server.service.ProcessService;
import no.difi.bcp.server.service.RegistrationService;
import no.difi.certvalidator.api.CertificateValidationException;
import no.difi.vefa.peppol.common.lang.PeppolParsingException;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
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
@PreAuthorize("hasAnyAuthority('USER')")
@RequestMapping("/certificate")
public class CertificateController {

    private static Base64.Encoder encoder = Base64.getEncoder();

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private ProcessService processService;

    @Autowired
    private RegistrationService registrationService;

    @RequestMapping(method = RequestMethod.GET)
    public String list(@AuthenticationPrincipal User user, @RequestParam(defaultValue = "1") int page,
                       ModelMap modelMap) throws BcpServerException {
        modelMap.put("list", certificateService.findAll(user.getParticipant(), page - 1));

        return "certificate/list";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String uploadForm(@AuthenticationPrincipal User user, ModelMap modelMap) throws BcpServerException {
        modelMap.put("form", new UploadForm());

        return "certificate/upload";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadSubmit(@AuthenticationPrincipal User user, @ModelAttribute("form") UploadForm form,
                               RedirectAttributes redirectAttributes) throws IOException, BcpServerException {
        try {
            certificateService.insert(user.getParticipant(), form);

            // redirectAttributes.addFlashAttribute("message", "You successfully uploaded " + file.getOriginalFilename() + "!");

            return "redirect:/certificate";
        } catch (CertificateValidationException | CertificateEncodingException e) {
            throw new CertificateException(e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/upload/simple", method = RequestMethod.POST)
    public String uploadSimple(@AuthenticationPrincipal User user, @RequestParam("file") MultipartFile file,
                               RedirectAttributes redirectAttributes) throws IOException, BcpServerException {
        try {
            certificateService.insert(user.getParticipant(), file.getInputStream());

            redirectAttributes.addFlashAttribute("message", "You successfully uploaded " + file.getOriginalFilename() + "!");

            return "redirect:/certificate";
        } catch (CertificateValidationException | CertificateEncodingException e) {
            throw new CertificateException(e.getMessage(), e);
        }
    }

    @PreAuthorize("#certificate.participant.id == principal.participant.id")
    @RequestMapping(value = "/{certificate}", method = RequestMethod.GET)
    public String view(@PathVariable Certificate certificate, ModelMap modelMap)
            throws BcpServerException {
        List<Process> processesConnected = processService.findByCertificate(certificate);
        List<Process> processesAvailable = processService.findAll();
        processesAvailable.removeAll(processesConnected);

        modelMap.put("certificate", certificate);
        modelMap.put("processesConnected", processesConnected);
        modelMap.put("processesAvailable", processesAvailable);

        return "certificate/view";
    }

    @PreAuthorize("true")
    @ResponseBody
    @RequestMapping(value = "/{certificate}/download", method = RequestMethod.GET)
    public void view(@PathVariable Certificate certificate, HttpServletResponse response) throws IOException, BcpServerException {
        response.setContentType("application/pkix-cert");
        response.setHeader("Content-Disposition", String.format(
                "attachment; filename=%s-%s.crt", certificate.getParticipant().getIdentifier(), certificate.getIdentifier()));

        Writer writer = response.getWriter();
        writer.write("-----BEGIN CERTIFICATE-----\r\n");
        writer.write(encoder.encodeToString(certificate.getCertificate()));
        writer.write("\r\n-----END CERTIFICATE-----");
        writer.flush();
    }

    @PreAuthorize("#certificate.participant.id == principal.participant.id")
    @RequestMapping(value = "/{certificate}/connect", method = RequestMethod.POST)
    public String connectProcess(@AuthenticationPrincipal User user, @PathVariable Certificate certificate,
                                 @RequestParam("process") String processParam) throws IOException, BcpServerException {
        try {
            Process process = processService.get(ProcessIdentifier.parse(processParam));

            Registration registration = new Registration();
            registration.setParticipant(user.getParticipant());
            registration.setProcess(process);
            registration.setCertificate(certificate);
            registrationService.save(registration);

            return String.format("redirect:/certificate/%s", certificate);
        } catch (PeppolParsingException e) {
            throw new InvalidInputException(e.getMessage(), e);
        }
    }

    @PreAuthorize("#certificate.participant.id == principal.participant.id")
    @RequestMapping(value = "/{certificate}/edit", method = RequestMethod.GET)
    public String editForm(@PathVariable Certificate certificate, ModelMap modelMap) {
        modelMap.put("form", new CertificateForm(certificate));

        return "certificate/form";
    }

    @PreAuthorize("#certificate.participant.id == principal.participant.id")
    @RequestMapping(value = "/{certificate}/edit", method = RequestMethod.POST)
    public String editSubmit(@PathVariable Certificate certificate, @ModelAttribute CertificateForm form,
                             BindingResult bindingResult, ModelMap modelMap)
            throws BcpServerException {
        if (bindingResult.hasErrors()) {
            modelMap.put("form", form);
            return "certificate/form";
        }

        certificateService.save(form.update(certificate));

        return "redirect:/certificate";
    }
}
