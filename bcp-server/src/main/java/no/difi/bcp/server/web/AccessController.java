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
import no.difi.bcp.server.lang.BcpServerException;
import no.difi.bcp.server.service.AccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * @author erlend
 */
@Controller
@RequestMapping("/access")
@PreAuthorize("isAnonymous()")
public class AccessController {

    @Autowired
    private AccessService accessService;

    @RequestMapping(method = RequestMethod.GET)
    public String uploadForm(ModelMap modelMap) {
        modelMap.put("form", new UploadForm());

        return "access/upload";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String uploadSubmit(@Valid @ModelAttribute("form") UploadForm form, BindingResult bindingResult, ModelMap modelMap) {
        if (bindingResult.hasErrors()) {
            modelMap.put("form", form);
            return "access/upload";
        }

        try {
            accessService.update(form.getFile().getInputStream());
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        } catch (BcpServerException e) {
            System.out.println(e.getMessage());
        }

        return "redirect:/access";
    }
}
