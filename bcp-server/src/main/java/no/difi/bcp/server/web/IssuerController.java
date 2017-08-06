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

import no.difi.bcp.server.domain.Issuer;
import no.difi.bcp.server.lang.BcpServerException;
import no.difi.bcp.server.service.IssuerService;
import no.difi.bcp.server.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Base64;

/**
 * @author erlend
 */
@Controller
@PreAuthorize("hasAnyAuthority('ADMIN')")
@RequestMapping("/issuer")
public class IssuerController {

    private static Base64.Encoder encoder = Base64.getEncoder();

    @Autowired
    private IssuerService issuerService;

    @Autowired
    private ValidationService validationService;

    @RequestMapping(method = RequestMethod.GET)
    public String list(ModelMap modelMap) {
        modelMap.put("list", issuerService.findAll());

        return "issuer/list";
    }

    @PreAuthorize("true")
    @ResponseBody
    @RequestMapping(value = "/{issuer}/download", method = RequestMethod.GET)
    public void download(@PathVariable Issuer issuer, HttpServletResponse response) throws IOException, BcpServerException {
        response.setContentType("application/pkix-cert");
        response.setHeader("Content-Disposition", String.format(
                "attachment; filename=issuer-%s.crt", issuer.getIdentifier()));

        Writer writer = response.getWriter();
        writer.write("-----BEGIN CERTIFICATE-----\r\n");
        writer.write(encoder.encodeToString(issuer.getCertificate()));
        writer.write("\r\n-----END CERTIFICATE-----");
        writer.flush();
    }

    @RequestMapping("/trigger")
    public String triggerValidation(RedirectAttributes redirectAttributes) {
        validationService.performValidation();

        redirectAttributes.addFlashAttribute("alert-success", "Validation of certificates triggered.");

        return "redirect:/issuer";
    }
}
