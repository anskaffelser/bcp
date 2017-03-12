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
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.virksert.server.domain.Participant;
import no.difi.virksert.server.domain.User;
import no.difi.virksert.server.form.UserForm;
import no.difi.virksert.server.lang.InvalidInputException;
import no.difi.virksert.server.lang.VirksertServerException;
import no.difi.virksert.server.service.ParticipantService;
import no.difi.virksert.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

/**
 * @author erlend
 */
@Controller
@RequestMapping("/participant/{participantParam}/user")
@PreAuthorize("hasAnyAuthority('ADMIN', #participantParam)")
public class ParticipantUserController {

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public String list(@PathVariable String participantParam, ModelMap modelMap) throws VirksertServerException {
        try {
            Participant participant = participantService.get(ParticipantIdentifier.parse(participantParam));

            modelMap.put("participant", participant);
            modelMap.put("list", userService.findByParticipant(participant));

            return "participant/user/list";
        } catch (PeppolParsingException e) {
            throw new InvalidInputException(e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addForm(@PathVariable String participantParam, ModelMap modelMap) throws VirksertServerException {
        try {
            Participant participant = participantService.get(ParticipantIdentifier.parse(participantParam));

            modelMap.put("participant", participant);
            modelMap.put("form", new UserForm());

            return "participant/user/form";
        } catch (PeppolParsingException e) {
            throw new InvalidInputException(e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addSubmit(@PathVariable String participantParam, @Valid UserForm form, BindingResult bindingResult,
                            ModelMap modelMap) throws VirksertServerException {
        try {
            Participant participant = participantService.get(ParticipantIdentifier.parse(participantParam));

            if (bindingResult.hasErrors()) {
                modelMap.put("participant", participant);
                modelMap.put("form", form);
                return "participant/user/form";
            }

            User user = form.update(new User());
            user.setParticipant(participant);
            userService.save(user);

            return String.format("redirect:/participant/%s/user", participantParam);
        } catch (PeppolParsingException e) {
            throw new InvalidInputException(e.getMessage(), e);
        }
    }
}
