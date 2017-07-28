/*
 *  Copyright 2017 Norwegian Agency for Public Management and eGovernment (Difi)
 *
 *  Licensed under the EUPL, Version 1.1 or – as soon they
 *  will be approved by the European Commission - subsequent
 *  versions of the EUPL (the "Licence");
 *
 *  You may not use this work except in compliance with the Licence.
 *
 *  You may obtain a copy of the Licence at:
 *
 *  https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 *  Unless required by applicable law or agreed to in
 *  writing, software distributed under the Licence is
 *  distributed on an "AS IS" basis,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *  express or implied.
 *  See the Licence for the specific language governing
 *  permissions and limitations under the Licence.
 */

package no.difi.bcp.server.web;

import no.difi.bcp.server.domain.Participant;
import no.difi.bcp.server.domain.User;
import no.difi.bcp.server.security.MasqueradeUser;
import no.difi.bcp.server.security.VirksertAuthenticationToken;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author erlend
 */
@Controller
@RequestMapping("/masquerade")
public class MasqueradeController {

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @RequestMapping(value = "/perform/{participant}", method = RequestMethod.GET)
    public String perform(@AuthenticationPrincipal User user, @PathVariable Participant participant,
                          RedirectAttributes redirectAttributes) {
        SecurityContextHolder.getContext()
                .setAuthentication(VirksertAuthenticationToken.newInstance(new MasqueradeUser(user, participant)));

        redirectAttributes.addFlashAttribute("alert-success",
                String.format("You are now acting on behalf of '%s'.", participant.getName()));

        return "redirect:/";
    }

    @PreAuthorize("hasAnyAuthority('MASQUERADE')")
    @RequestMapping(value = "/back", method = RequestMethod.GET)
    public String logout(@AuthenticationPrincipal MasqueradeUser user, RedirectAttributes redirectAttributes) {
        SecurityContextHolder.getContext()
                .setAuthentication(VirksertAuthenticationToken.newInstance(user.getOriginal()));

        redirectAttributes.addFlashAttribute("alert-warning", "You are now acting as administrator.");

        return "redirect:/";
    }
}
