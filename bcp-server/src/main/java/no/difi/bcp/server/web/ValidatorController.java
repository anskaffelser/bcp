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

package no.difi.bcp.server.web;

import no.difi.bcp.server.form.UploadForm;
import no.difi.certvalidator.ValidatorGroup;
import no.difi.certvalidator.api.CertificateValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.security.cert.X509Certificate;

/**
 * @author erlend
 */
@Controller
@RequestMapping("/validator")
public class ValidatorController {

    @Autowired
    private ValidatorGroup validatorGroup;

    @RequestMapping(method = RequestMethod.GET)
    public String viewForm(ModelMap modelMap) {
        modelMap.put("form", new UploadForm());

        return "validator/upload";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String viewSubmit(@Valid @ModelAttribute("form") UploadForm form, RedirectAttributes redirectAttributes)
            throws IOException, CertificateValidationException {
        X509Certificate certificate = ValidatorGroup.getCertificate(form.getFile().getInputStream());

        if (validatorGroup.isValid(certificate)) {
            redirectAttributes.addFlashAttribute("alert-success",
                    String.format("Certificate '%s' is valid.", certificate.getSubjectX500Principal().getName()));
        } else {
            redirectAttributes.addFlashAttribute("alert-danger",
                    String.format("Certificate '%s' is invalid.", certificate.getSubjectX500Principal().getName()));
        }

        return "redirect:/validator";
    }
}
