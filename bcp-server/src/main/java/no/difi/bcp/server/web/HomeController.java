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

import no.difi.bcp.server.domain.User;
import no.difi.bcp.server.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author erlend
 */
@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    private ApplicationService applicationService;

    @RequestMapping(method = RequestMethod.GET)
    public String home(@AuthenticationPrincipal User user, ModelMap modelMap) {
        if (user == null) {
            // Anonymous

            return "home_anonymous";
        } else if (user.getParticipant() == null) {
            // Admin

            return "home_admin";
        } else {
            // User
            modelMap.put("applications", applicationService.findByCustomer(user.getParticipant()));

            return "home_user";
        }
    }

    @RequestMapping(value = "/favicon.ico", method = RequestMethod.GET)
    public String favicon() {
        return "forward:/resources/favicon.ico";
    }
}
