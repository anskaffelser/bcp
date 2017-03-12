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

import no.difi.vefa.peppol.common.lang.PeppolParsingException;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import no.difi.virksert.server.domain.Process;
import no.difi.virksert.server.form.ProcessForm;
import no.difi.virksert.server.lang.InvalidInputException;
import no.difi.virksert.server.lang.VirksertServerException;
import no.difi.virksert.server.service.ProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static Logger logger = LoggerFactory.getLogger(ProcessController.class);

    @Autowired
    private ProcessService processService;

    @PreAuthorize("true")
    @RequestMapping(method = RequestMethod.GET)
    public String list(@RequestParam(defaultValue = "1") int page, ModelMap modelMap) {
        modelMap.put("list", processService.findAll(page - 1));

        return "process/list";
    }

    @PreAuthorize("true")
    @RequestMapping(value = "/{processParam:.+}", method = RequestMethod.GET)
    public String view(@PathVariable String processParam, ModelMap modelMap)
            throws VirksertServerException {
        try {
            ProcessIdentifier processIdentifier = ProcessIdentifier.parse(processParam);
            logger.info("{}", processIdentifier);

            Process process = processService.get(processIdentifier);

            logger.info("{}", process);

            modelMap.put("process", process);

            return "process/view";
        } catch (PeppolParsingException e) {
            throw new InvalidInputException(e.getMessage(), e);
        }
    }


    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addForm(ModelMap modelMap) {
        modelMap.put("form", new ProcessForm());

        return "process/form";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addSubmit(@ModelAttribute ProcessForm form, BindingResult bindingResult, ModelMap modelMap) {
        if (bindingResult.hasErrors()) {
            modelMap.put("form", form);
            return "process/form";
        }

        Process process = form.update(new Process());
        processService.save(process);

        return "redirect:/process";
    }
}
