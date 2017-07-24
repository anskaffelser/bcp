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

import no.difi.virksert.server.domain.User;
import no.difi.virksert.server.form.UserForm;
import no.difi.virksert.server.lang.VirksertServerException;
import no.difi.virksert.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/user")
@PreAuthorize("isAuthenticated()")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public String list(@AuthenticationPrincipal User principal, ModelMap modelMap) {
        modelMap.put("list", userService.findByParticipant(principal.getParticipant()));

        return "user/list";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addForm(ModelMap modelMap) {
        modelMap.put("form", new UserForm());

        return "user/form";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addSubmit(@AuthenticationPrincipal User principal, @Valid UserForm form, BindingResult bindingResult, ModelMap modelMap) {
        if (bindingResult.hasErrors()) {
            modelMap.put("form", form);
            return "user/form";
        }

        User user = form.update(new User());
        user.setParticipant(principal.getParticipant());
        userService.save(user);

        return "redirect:/user";
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.GET)
    public String view(@AuthenticationPrincipal User principal, @PathVariable String identifier, ModelMap modelMap) throws VirksertServerException {
        modelMap.put("user", userService.findUserByIdentifier(principal.getParticipant(), identifier));

        return "user/view";
    }

    @RequestMapping(value = "/{identifier}/edit", method = RequestMethod.GET)
    public String editForm(@AuthenticationPrincipal User principal, @PathVariable String identifier, ModelMap modelMap) throws VirksertServerException {
        User user = userService.findUserByIdentifier(principal.getParticipant(), identifier);

        modelMap.put("form", new UserForm(user));
        modelMap.put("user", user);

        return "user/form";
    }

    @RequestMapping(value = "/{identifier}/edit", method = RequestMethod.POST)
    public String editSubmit(@AuthenticationPrincipal User principal, @PathVariable String identifier, @Valid UserForm form, BindingResult bindingResult,
                             ModelMap modelMap) throws VirksertServerException {
        User user = userService.findUserByIdentifier(principal.getParticipant(), identifier);

        if (bindingResult.hasErrors()) {
            form.setExists(true);
            modelMap.put("form", form);
            modelMap.put("user", user);
            return "user/form";
        }

        user = form.update(user);
        userService.save(user);

        return "redirect:/user";
    }

    @RequestMapping(value = "/{identifier}/delete", method = RequestMethod.GET)
    public String deleteForm(@AuthenticationPrincipal User principal, @PathVariable String identifier, ModelMap modelMap) throws VirksertServerException {
        User user = userService.findUserByIdentifier(principal.getParticipant(), identifier);

        modelMap.put("user", user);

        return "user/delete";
    }

    @RequestMapping(value = "/{identifier}/delete", method = RequestMethod.POST)
    public String deleteSubmit(@AuthenticationPrincipal User principal, @PathVariable String identifier, ModelMap modelMap) throws VirksertServerException {
        User user = userService.findUserByIdentifier(principal.getParticipant(), identifier);
        userService.delete(user);

        return "redirect:/user";
    }
}
