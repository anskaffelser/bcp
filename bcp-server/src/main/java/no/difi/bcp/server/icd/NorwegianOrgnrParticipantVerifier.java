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

package no.difi.bcp.server.icd;

import no.difi.bcp.server.api.ParticipantVerifier;
import no.difi.bcp.server.domain.Participant;
import no.difi.bcp.server.lang.BcpServerException;
import no.difi.bcp.server.service.DatahotelService;
import no.difi.bcp.server.util.DatahotelOrganization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * @author erlend
 */
@Component
public class NorwegianOrgnrParticipantVerifier implements ParticipantVerifier {

    private static final String ICD = "9908";

    private static final Pattern PATTERN = Pattern.compile("9908:[0-9]{9}");

    @Autowired
    private DatahotelService datahotelService;

    @Override
    public boolean supported(String icd) {
        return ICD.equals(icd);
    }

    @Override
    public boolean isValid(String identifier) {
        return PATTERN.matcher(identifier).matches();
    }

    @Override
    public void update(Participant participant) throws BcpServerException {
        datahotelService.findByIdentifier(participant.getIdentifier().substring(5))
                .map(DatahotelOrganization::getNavn)
                .ifPresent(participant::setName);
    }
}
