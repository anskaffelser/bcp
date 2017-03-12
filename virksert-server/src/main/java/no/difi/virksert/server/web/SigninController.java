package no.difi.virksert.server.web;

import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.virksert.server.domain.Participant;
import no.difi.virksert.server.form.SigninCodeForm;
import no.difi.virksert.server.form.SigninEmailForm;
import no.difi.virksert.server.lang.ParticipantNotFoundException;
import no.difi.virksert.server.service.LoginService;
import no.difi.virksert.server.service.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String emailSubmit(@Valid SigninEmailForm form, BindingResult bindingResult,
                              ModelMap modelMap) throws ParticipantNotFoundException {
        if (bindingResult.hasErrors()) {
            modelMap.put("form", form);
            return "signin/email";
        }

        Participant participant = null;
        if (!"admin".equals(form.getParticipant()))
            participant = participantService.get(ParticipantIdentifier.of(form.getParticipant()));

        loginService.prepare(participant, form.getEmail());

        return String.format("redirect:/signin/code?participant=%s", form.getParticipant());
    }
}
