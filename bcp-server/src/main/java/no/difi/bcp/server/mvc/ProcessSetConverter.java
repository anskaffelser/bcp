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

package no.difi.bcp.server.mvc;

import no.difi.bcp.api.Role;
import no.difi.bcp.server.form.ApplicationProcessForm;
import no.difi.bcp.server.lang.ProcessNotFoundException;
import no.difi.bcp.server.service.ProcessService;
import no.difi.vefa.peppol.common.lang.PeppolParsingException;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * @author erlend
 */
@Component
public class ProcessSetConverter implements Converter<String, ApplicationProcessForm.ProcessSet> {

    @Autowired
    private ProcessService processService;

    @Override
    public ApplicationProcessForm.ProcessSet convert(String s) {
        try {
            String[] parts = s.split("/");

            return new ApplicationProcessForm.ProcessSet(
                    processService.get(ProcessIdentifier.parse(parts[0])),
                    Role.valueOf(parts[1])
            );
        } catch (PeppolParsingException | ProcessNotFoundException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }
}
