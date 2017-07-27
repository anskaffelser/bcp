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

import no.difi.bcp.server.domain.Application;
import no.difi.bcp.server.domain.User;
import no.difi.bcp.server.form.ApplicationCertificateForm;
import no.difi.bcp.server.form.ApplicationForm;
import no.difi.bcp.server.lang.BcpServerException;
import no.difi.bcp.server.service.ApplicationService;
import no.difi.bcp.server.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author erlend
 */
@Controller
@PreAuthorize("hasAnyAuthority('USER')")
@RequestMapping("/application")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CertificateService certificateService;

    @RequestMapping(method = RequestMethod.GET)
    public String list(@AuthenticationPrincipal User user, ModelMap modelMap) {
        modelMap.put("list", applicationService.findByParticipant(user.getParticipant()));

        return "application/list";
    }

    @PreAuthorize("#app.participant.id == principal.participant.id")
    @RequestMapping(value = "/{app}", method = RequestMethod.GET)
    public String view(@PathVariable Application app, ModelMap modelMap) {
        modelMap.put("item", app);

        return "application/view";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addForm(ModelMap modelMap) {
        modelMap.put("form", new ApplicationForm());

        return "application/form";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addSubmit(@AuthenticationPrincipal User user, @ModelAttribute ApplicationForm form,
                            BindingResult bindingResult, ModelMap modelMap)
            throws BcpServerException {
        if (bindingResult.hasErrors()) {
            modelMap.put("form", form);
            return "application/form";
        }

        Application application = form.update(Application.newInstance());
        application.setParticipant(user.getParticipant());
        applicationService.save(application);

        return "redirect:/application";
    }

    @PreAuthorize("#app.participant.id == principal.participant.id")
    @RequestMapping(value = "/{app}/edit", method = RequestMethod.GET)
    public String editForm(@PathVariable Application app, ModelMap modelMap) {
        modelMap.put("form", new ApplicationForm(app));

        return "application/form";
    }

    @PreAuthorize("#app.participant.id == principal.participant.id")
    @RequestMapping(value = "/{app}/edit", method = RequestMethod.POST)
    public String editSubmit(@PathVariable Application app, @ModelAttribute ApplicationForm form,
                             BindingResult bindingResult, ModelMap modelMap) {
        if (bindingResult.hasErrors()) {
            modelMap.put("form", form);
            return "participant/form";
        }

        applicationService.save(form.update(app));

        return "redirect:/application";
    }

    @PreAuthorize("#app.participant.id == principal.participant.id")
    @RequestMapping(value = "/{app}/certificates", method = RequestMethod.GET)
    public String certificatesForm(@PathVariable Application app, ModelMap modelMap) {
        modelMap.put("form", new ApplicationCertificateForm(app.getCertificates()));
        modelMap.put("item", app);
        modelMap.put("list", certificateService.findAll(app.getParticipant()));

        return "application/certificates";
    }

    @PreAuthorize("#app.participant.id == principal.participant.id")
    @RequestMapping(value = "/{app}/certificates", method = RequestMethod.POST)
    public String certificatesSubmit(@PathVariable Application app, @ModelAttribute ApplicationCertificateForm form,
                                     BindingResult bindingResult, ModelMap modelMap) {
        if (bindingResult.hasErrors()) {
            modelMap.put("form", form);
            modelMap.put("item", app);
            modelMap.put("list", certificateService.findAll(app.getParticipant()));
            return "application/certificates";
        }

        applicationService.save(form.update(app));

        return String.format("redirect:/application/%s", app.getIdentifier());
    }
}