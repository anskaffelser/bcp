package no.difi.virksert.server.web;

import no.difi.vefa.peppol.common.lang.PeppolParsingException;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.virksert.server.domain.Participant;
import no.difi.virksert.server.form.ParticipantForm;
import no.difi.virksert.server.lang.InvalidInputException;
import no.difi.virksert.server.lang.VirksertServerException;
import no.difi.virksert.server.service.CertificateService;
import no.difi.virksert.server.service.ParticipantService;
import no.difi.virksert.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * @author erlend
 */
@Controller
@RequestMapping("/participant")
@PreAuthorize("hasAnyAuthority('ADMIN')")
public class ParticipantController {

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private UserService userService;

    @PreAuthorize("true")
    @RequestMapping(method = RequestMethod.GET)
    public String listParticipants(@RequestParam(defaultValue = "1") int page, ModelMap modelMap) {
        modelMap.put("list", participantService.findAll(page - 1));

        return "participant/list";
    }

    @PreAuthorize("true")
    @RequestMapping(value = "/{participantParam}", method = RequestMethod.GET)
    public String view(@PathVariable String participantParam, ModelMap modelMap) throws VirksertServerException {
        try {
            ParticipantIdentifier participantIdentifier = ParticipantIdentifier.parse(participantParam);
            Participant participant = participantService.get(participantIdentifier);

            modelMap.put("participant", participant);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication.getAuthorities().stream().map(Object::toString)
                    .anyMatch(s -> participantIdentifier.toString().equals(s) || "ADMIN".equals(s))) {

                modelMap.put("count_certificates", certificateService.countActive(participant));
                modelMap.put("count_users", userService.count(participant));

                return "participant/view";
            } else {
                return "participant/view_public";
            }
        } catch (PeppolParsingException e) {
            throw new InvalidInputException(e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addForm(ModelMap modelMap) {
        modelMap.put("form", new ParticipantForm());

        return "participant/form";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addSubmit(@ModelAttribute ParticipantForm form, BindingResult bindingResult, ModelMap modelMap)
            throws VirksertServerException {
        if (bindingResult.hasErrors()) {
            modelMap.put("form", form);
            return "participant/form";
        }

        Participant process = form.update(new Participant());
        participantService.save(process);

        return "redirect:/participant";
    }
}
