package no.difi.virksert.server.web;

import no.difi.virksert.server.domain.User;
import no.difi.virksert.server.form.UserForm;
import no.difi.virksert.server.lang.VirksertServerException;
import no.difi.virksert.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

/**
 * @author erlend
 */
@Controller
@RequestMapping("/user")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public String list(ModelMap modelMap) {
        modelMap.put("list", userService.findByParticipant(null));

        return "user/list";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addForm(ModelMap modelMap) throws VirksertServerException {
        modelMap.put("form", new UserForm());

        return "participant/user/form";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addSubmit(@Valid UserForm form, BindingResult bindingResult, ModelMap modelMap)
            throws VirksertServerException {
        if (bindingResult.hasErrors()) {
            modelMap.put("form", form);
            return "participant/user/form";
        }

        User user = form.update(new User());
        userService.save(user);

        return "redirect:/user";
    }
}
