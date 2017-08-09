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

import no.difi.bcp.jaxb.registration.LoginType;
import no.difi.bcp.jaxb.registration.ObjectFactory;
import no.difi.bcp.jaxb.registration.RegistrationType;
import no.difi.bcp.jaxb.registration.UserType;
import no.difi.bcp.server.domain.User;
import no.difi.bcp.server.form.UserForm;
import no.difi.bcp.server.lang.BcpServerException;
import no.difi.bcp.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * @author erlend
 */
@Controller
@RequestMapping("/user")
@PreAuthorize("isAuthenticated()")
public class UserController {

    private static final JAXBContext JAXB_CONTEXT;

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(RegistrationType.class);
        } catch (JAXBException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

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
    public String addSubmit(@AuthenticationPrincipal User principal, @Valid @ModelAttribute("form") UserForm form,
                            BindingResult bindingResult, ModelMap modelMap) {
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
    public String view(@AuthenticationPrincipal User principal, @PathVariable String identifier, ModelMap modelMap)
            throws BcpServerException {
        modelMap.put("user", userService.findUserByIdentifier(principal.getParticipant(), identifier));

        return "user/view";
    }

    @RequestMapping(value = "/{identifier}/edit", method = RequestMethod.GET)
    public String editForm(@AuthenticationPrincipal User principal, @PathVariable String identifier, ModelMap modelMap)
            throws BcpServerException {
        User user = userService.findUserByIdentifier(principal.getParticipant(), identifier);

        modelMap.put("form", new UserForm(user));
        modelMap.put("user", user);

        return "user/form";
    }

    @RequestMapping(value = "/{identifier}/edit", method = RequestMethod.POST)
    public String editSubmit(@AuthenticationPrincipal User principal, @PathVariable String identifier,
                             @Valid @ModelAttribute("form") UserForm form, BindingResult bindingResult,
                             ModelMap modelMap) throws BcpServerException {
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
    public String deleteForm(@AuthenticationPrincipal User principal, @PathVariable String identifier, ModelMap modelMap)
            throws BcpServerException {
        User user = userService.findUserByIdentifier(principal.getParticipant(), identifier);

        modelMap.put("user", user);

        return "user/delete";
    }

    @RequestMapping(value = "/{identifier}/delete", method = RequestMethod.POST)
    public String deleteSubmit(@AuthenticationPrincipal User principal, @PathVariable String identifier,
                               ModelMap modelMap) throws BcpServerException {
        User user = userService.findUserByIdentifier(principal.getParticipant(), identifier);
        userService.delete(user);

        return "redirect:/user";
    }

    @ResponseBody
    @RequestMapping(value = "/xml", method = RequestMethod.GET)
    public void xmlDump(@AuthenticationPrincipal User principal, HttpServletResponse response) throws JAXBException, IOException {

        RegistrationType registration = OBJECT_FACTORY.createRegistrationType();

        registration.getUser().addAll(
                userService.findByParticipant(principal.getParticipant()).stream()
                .map(user -> {
                    UserType userType = OBJECT_FACTORY.createUserType();
                    userType.setName(user.getName());
                    userType.setEmail(user.getEmail());

                    LoginType loginType = OBJECT_FACTORY.createLoginType();
                    loginType.setType("email");
                    loginType.setValue(user.getEmail());
                    userType.setLogin(loginType);

                    return userType;
                })
                .collect(Collectors.toList()));

        response.setContentType(MediaType.APPLICATION_XML_VALUE);

        Marshaller marshaller = JAXB_CONTEXT.createMarshaller();
        marshaller.marshal(OBJECT_FACTORY.createRegistration(registration), response.getWriter());
    }
}
