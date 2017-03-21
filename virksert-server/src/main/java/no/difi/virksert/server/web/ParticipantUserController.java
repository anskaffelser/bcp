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

import no.difi.virksert.server.domain.Participant;
import no.difi.virksert.server.domain.User;
import no.difi.virksert.server.form.UserForm;
import no.difi.virksert.server.lang.VirksertServerException;
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
@RequestMapping("/participant/{participant}/user")
@PreAuthorize("hasAnyAuthority('ADMIN', #participant.toVefa().toString())")
public class ParticipantUserController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public String list(@PathVariable Participant participant, ModelMap modelMap) throws VirksertServerException {
        modelMap.put("participant", participant);
        modelMap.put("list", userService.findByParticipant(participant));

        return "participant/user/list";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addForm(@PathVariable Participant participant, ModelMap modelMap) throws VirksertServerException {
        modelMap.put("participant", participant);
        modelMap.put("form", new UserForm());

        return "participant/user/form";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addSubmit(@PathVariable Participant participant, @Valid UserForm form, BindingResult bindingResult,
                            ModelMap modelMap) throws VirksertServerException {
        if (bindingResult.hasErrors()) {
            modelMap.put("participant", participant);
            modelMap.put("form", form);
            return "participant/user/form";
        }

        User user = form.update(new User());
        user.setParticipant(participant);
        userService.save(user);

        return String.format("redirect:/participant/%s/user", participant.toVefa());
    }
}
