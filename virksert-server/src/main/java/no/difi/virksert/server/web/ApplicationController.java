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

package no.difi.virksert.server.web;

import no.difi.virksert.server.domain.Application;
import no.difi.virksert.server.domain.User;
import no.difi.virksert.server.form.ApplicationForm;
import no.difi.virksert.server.lang.VirksertServerException;
import no.difi.virksert.server.service.ApplicationService;
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

    @RequestMapping(method = RequestMethod.GET)
    public String list(@AuthenticationPrincipal User user, ModelMap modelMap) {
        modelMap.put("list", applicationService.findByParticipant(user.getParticipant()));

        return "application/list";
    }

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
            throws VirksertServerException {
        if (bindingResult.hasErrors()) {
            modelMap.put("form", form);
            return "application/form";
        }

        Application application = form.update(Application.newInstance());
        application.setParticipant(user.getParticipant());
        applicationService.save(application);

        return "redirect:/application";
    }

    @RequestMapping(value = "/{app}/edit", method = RequestMethod.GET)
    public String editForm(@PathVariable Application app, ModelMap modelMap) {
        modelMap.put("form", new ApplicationForm(app));

        return "application/form";
    }

    @RequestMapping(value = "/{app}/edit", method = RequestMethod.POST)
    public String editSubmit(@PathVariable Application app, @ModelAttribute ApplicationForm form,
                             BindingResult bindingResult, ModelMap modelMap)
            throws VirksertServerException {
        if (bindingResult.hasErrors()) {
            modelMap.put("form", form);
            return "participant/form";
        }

        applicationService.save(form.update(app));

        return "redirect:/application";
    }

}
