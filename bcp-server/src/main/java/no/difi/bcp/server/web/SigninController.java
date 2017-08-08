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
import no.difi.bcp.server.form.SigninCodeForm;
import no.difi.bcp.server.form.SigninEmailForm;
import no.difi.bcp.server.lang.ParticipantNotFoundException;
import no.difi.bcp.server.service.LoginService;
import no.difi.bcp.server.service.ParticipantService;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

/**
 * @author erlend
 */
@Controller
@RequestMapping("/signin")
@PreAuthorize("isAnonymous()")
public class SigninController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private ParticipantService participantService;

    @Value("${bcp.login.show_code:false}")
    private boolean showCode;

    @RequestMapping(method = RequestMethod.GET)
    public String viewIndex() {
        return "redirect:/signin/email";
    }

    @RequestMapping(value = "/code", method = RequestMethod.GET)
    public String codeForm(@RequestParam(required = false) String participant, ModelMap modelMap) {
        modelMap.put("form", new SigninCodeForm(participant));

        return "signin/code";
    }

    @RequestMapping(value = "/email", method = RequestMethod.GET)
    public String emailForm(ModelMap modelMap) {
        modelMap.put("form", new SigninEmailForm());

        return "signin/email";
    }

    @RequestMapping(value = "/email", method = RequestMethod.POST)
    public String emailSubmit(@Valid @ModelAttribute("form") SigninEmailForm form, BindingResult bindingResult,
                              ModelMap modelMap, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            modelMap.put("form", form);
            return "signin/email";
        }

        Participant participant = null;
        try {
            if (!"admin".equals(form.getParticipant()))
                participant = participantService.get(ParticipantIdentifier.of(form.getParticipant()));
        } catch (ParticipantNotFoundException e) {
            redirectAttributes.addFlashAttribute("alert-warning",
                    String.format("Participant identifier '%s' not found.", form.getParticipant()));

            return "redirect:/signin/email";
        }

        String code = loginService.prepare(participant, form.getEmail());

        if (code != null && showCode)
            redirectAttributes.addFlashAttribute("code", code);

        return String.format("redirect:/signin/code?participant=%s", form.getParticipant());
    }
}
