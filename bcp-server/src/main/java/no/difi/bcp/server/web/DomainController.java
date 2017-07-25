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

import no.difi.bcp.server.domain.Domain;
import no.difi.bcp.server.form.DomainForm;
import no.difi.bcp.server.lang.VirksertServerException;
import no.difi.bcp.server.service.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
@PreAuthorize("hasAnyAuthority('ADMIN')")
@RequestMapping("/domain")
public class DomainController {

    @Autowired
    private DomainService domainService;

    @RequestMapping(method = RequestMethod.GET)
    public String listParticipants(ModelMap modelMap) {
        modelMap.put("list", domainService.findAll());

        return "domain/list";
    }

    @RequestMapping(value = "/{domain}", method = RequestMethod.GET)
    public String view(@PathVariable Domain domain, ModelMap modelMap) {
        modelMap.put("domain", domain);

        return "domain/view";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addForm(ModelMap modelMap) {
        modelMap.put("form", new DomainForm());

        return "domain/form";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addSubmit(@ModelAttribute DomainForm form, BindingResult bindingResult, ModelMap modelMap)
            throws VirksertServerException {
        if (bindingResult.hasErrors()) {
            modelMap.put("form", form);
            return "participant/form";
        }

        domainService.save(form.update(Domain.newInstance()));

        return "redirect:/domain";
    }

    @RequestMapping(value = "/{domain}/edit", method = RequestMethod.GET)
    public String editForm(@PathVariable Domain domain, ModelMap modelMap) {
        modelMap.put("form", new DomainForm(domain));

        return "domain/form";
    }

    @RequestMapping(value = "/{domain}/edit", method = RequestMethod.POST)
    public String editSubmit(@PathVariable Domain domain, @ModelAttribute DomainForm form, BindingResult bindingResult, ModelMap modelMap)
            throws VirksertServerException {
        if (bindingResult.hasErrors()) {
            modelMap.put("form", form);
            return "participant/form";
        }

        domainService.save(form.update(domain));

        return "redirect:/domain";
    }

    @RequestMapping(value = "/{domain}/delete", method = RequestMethod.GET)
    public String deleteForm(@PathVariable Domain domain, ModelMap modelMap) throws VirksertServerException {
        modelMap.put("domain", domain);

        return "domain/delete";
    }

    @RequestMapping(value = "/{domain}/delete", method = RequestMethod.POST)
    public String deleteSubmit(@PathVariable Domain domain) throws VirksertServerException {
        // processService.delete(domain);

        return "redirect:/domain";
    }
}
