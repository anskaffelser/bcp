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

import no.difi.bcp.server.domain.Participant;
import no.difi.bcp.server.domain.User;
import no.difi.bcp.server.form.SepApplicationForm;
import no.difi.bcp.server.form.SepForm;
import no.difi.bcp.server.lang.BcpServerException;
import no.difi.bcp.server.service.ApplicationService;
import no.difi.bcp.server.service.ParticipantService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

/**
 * @author erlend
 */
@Controller
@RequestMapping("/sep")
@PreAuthorize("hasAnyAuthority('USER')")
public class SepController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ParticipantService participantService;

    @RequestMapping(method = RequestMethod.GET)
    public String list(@AuthenticationPrincipal User user, ModelMap modelMap) {
        modelMap.put("list", participantService.findByOwnership(user.getParticipant()));

        return "sep/list";
    }

    @PreAuthorize("#participant == principal.participant or #participant.parent == principal.participant")
    @RequestMapping("/{participant}")
    public String view(@PathVariable Participant participant, ModelMap modelMap) {
        modelMap.put("participant", participant);
        modelMap.put("applications", applicationService.findByCustomer(participant));

        return "sep/view";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addForm(ModelMap modelMap) {
        modelMap.put("form", new SepForm());

        return "sep/form";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addSubmit(@AuthenticationPrincipal User user, @Valid @ModelAttribute("form") SepForm form,
                            BindingResult bindingResult, ModelMap modelMap, RedirectAttributes redirectAttributes)
            throws BcpServerException {
        if (bindingResult.hasErrors()) {
            modelMap.put("form", form);
            return "sep/form";
        }

        participantService.createSep(user.getParticipant(), form);

        redirectAttributes.addFlashAttribute("alert-success",
                String.format("Sub Exchange Participant '%s' successfully registered.", form.getName()));

        return "redirect:/sep";
    }

    @PreAuthorize("#participant == principal.participant or #participant.parent == principal.participant")
    @RequestMapping(value = "/{participant}/apps", method = RequestMethod.GET)
    public String appsForm(@PathVariable Participant participant, ModelMap modelMap) {
        modelMap.put("participant", participant);
        modelMap.put("list", applicationService.findByParticipant(participant.getParent() == null ? participant : participant.getParent()));
        modelMap.put("form", new SepApplicationForm(applicationService.findByCustomer(participant)));

        return "sep/apps";
    }


    @PreAuthorize("#participant == principal.participant or #participant.parent == principal.participant")
    @RequestMapping(value = "/{participant}/apps", method = RequestMethod.POST)
    public String appsSubmit(@PathVariable Participant participant, @Valid @ModelAttribute("form") SepApplicationForm form,
                             BindingResult bindingResult, ModelMap modelMap, RedirectAttributes redirectAttributes)
            throws BcpServerException {
        if (bindingResult.hasErrors()) {
            modelMap.put("participant", participant);
            modelMap.put("list", applicationService.findByParticipant(participant.getParent() == null ? participant : participant.getParent()));
            modelMap.put("form", form);
            return "sep/form";
        }

        participant.setApplications(form.getApplications());
        participantService.save(participant);

        return String.format("redirect:/sep/%s", participant.toVefa().toString());
    }
}
