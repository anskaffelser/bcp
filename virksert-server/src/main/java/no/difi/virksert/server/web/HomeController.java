package no.difi.virksert.server.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author erlend
 */
@Controller
@RequestMapping("/")
public class HomeController {

    @RequestMapping
    public String home() {
        return "home";
    }

}
