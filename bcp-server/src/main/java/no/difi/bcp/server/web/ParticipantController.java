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

import no.difi.bcp.server.domain.Participant;
import no.difi.bcp.server.form.ParticipantForm;
import no.difi.bcp.server.lang.BcpServerException;
import no.difi.bcp.server.service.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

/**
 * @author erlend
 */
@Controller
@RequestMapping("/participant")
@PreAuthorize("hasAnyAuthority('ADMIN')")
public class ParticipantController {

    @Autowired
    private ParticipantService participantService;

    @RequestMapping(method = RequestMethod.GET)
    public String listParticipants(@RequestParam(defaultValue = "1") int page, ModelMap modelMap) {
        modelMap.put("list", participantService.findAll(page - 1));

        return "participant/list";
    }

    @RequestMapping(value = "/{participant}", method = RequestMethod.GET)
    public String view(@PathVariable Participant participant, ModelMap modelMap) throws BcpServerException {
        modelMap.put("participant", participant);

        return "participant/view";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addForm(ModelMap modelMap) {
        modelMap.put("form", new ParticipantForm());

        return "participant/form";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addSubmit(@Valid @ModelAttribute("form") ParticipantForm form, BindingResult bindingResult,
                            ModelMap modelMap, RedirectAttributes redirectAttributes)
            throws BcpServerException {
        if (bindingResult.hasErrors()) {
            modelMap.put("form", form);
            return "participant/form";
        }

        Participant participant = participantService.save(form.update(new Participant()));

        redirectAttributes.addFlashAttribute("alert-success",
                String.format("Participant '%s' successfully registered.", participant.getName()));

        return "redirect:/participant";
    }
}
