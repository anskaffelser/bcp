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

import no.difi.bcp.server.domain.Process;
import no.difi.bcp.server.form.ProcessForm;
import no.difi.bcp.server.lang.VirksertServerException;
import no.difi.bcp.server.service.DomainService;
import no.difi.bcp.server.service.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * @author erlend
 */
@Controller
@RequestMapping("/process")
@PreAuthorize("hasAnyAuthority('ADMIN')")
public class ProcessController {

    @Autowired
    private DomainService domainService;

    @Autowired
    private ProcessService processService;

    @PreAuthorize("true")
    @RequestMapping(method = RequestMethod.GET)
    public String list(@RequestParam(defaultValue = "1") int page, ModelMap modelMap) {
        modelMap.put("list", processService.findAll(page - 1));

        return "process/list";
    }

    @PreAuthorize("true")
    @RequestMapping(value = "/{process:.+}", method = RequestMethod.GET)
    public String view(@PathVariable Process process, ModelMap modelMap) {
        modelMap.put("process", process);

        return "process/view";
    }


    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addForm(ModelMap modelMap) {
        modelMap.put("domains", domainService.findAll());
        modelMap.put("form", new ProcessForm());

        return "process/form";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addSubmit(@ModelAttribute ProcessForm form, BindingResult bindingResult, ModelMap modelMap) {
        if (bindingResult.hasErrors()) {
            modelMap.put("domains", domainService.findAll());
            modelMap.put("form", form);
            return "process/form";
        }

        processService.save(form.update(new Process()));

        return "redirect:/process";
    }

    @RequestMapping(value = "/{process:.+}/edit", method = RequestMethod.GET)
    public String editForm(@PathVariable Process process, ModelMap modelMap) {
        modelMap.put("domains", domainService.findAll());
        modelMap.put("form", new ProcessForm(process));

        return "process/form";
    }

    @RequestMapping(value = "/{process:.+}/edit", method = RequestMethod.POST)
    public String editSubmit(@PathVariable Process process, @ModelAttribute ProcessForm form, BindingResult bindingResult, ModelMap modelMap) {
        if (bindingResult.hasErrors()) {
            modelMap.put("domains", domainService.findAll());
            modelMap.put("form", form);
            return "process/form";
        }

        processService.save(form.update(process));

        return "redirect:/process";
    }

    @RequestMapping(value = "/{process:.+}/delete", method = RequestMethod.GET)
    public String deleteForm(@PathVariable Process process, ModelMap modelMap) throws VirksertServerException {
        modelMap.put("process", process);

        return "process/delete";
    }

    @RequestMapping(value = "/{process:.+}/delete", method = RequestMethod.POST)
    public String deleteSubmit(@PathVariable Process process) throws VirksertServerException {
        processService.delete(process);

        return "redirect:/process";
    }
}
