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

package no.difi.virksert.server.service;

import no.difi.datahotel.client.Datahotel;
import no.difi.datahotel.client.DatahotelBuilder;
import no.difi.datahotel.client.Result;
import no.difi.datahotel.client.lang.DatahotelException;
import no.difi.virksert.server.lang.RemoteDatasourceException;
import no.difi.virksert.server.lang.VirksertServerException;
import no.difi.virksert.server.util.DatahotelOrganization;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author erlend
 */
@Service
public class DatahotelService {

    private Datahotel<DatahotelOrganization> enhetsregisteret =
            DatahotelBuilder.create(DatahotelOrganization.class, "brreg/enhetsregisteret").build();

    private Datahotel<DatahotelOrganization> underenheter =
            DatahotelBuilder.create(DatahotelOrganization.class, "brreg/underenheter").build();

    public Optional<DatahotelOrganization> findByIdentifier(String identifier) throws VirksertServerException {
        try {
            Result<DatahotelOrganization> result = enhetsregisteret.field("orgnr", identifier).fetch();

            if (result.size() == 1)
                return Optional.of(result.get(0));

            result = underenheter.field("orgnr", identifier).fetch();

            if (result.size() == 1)
                return Optional.of(result.get(0));

            return Optional.empty();
        } catch (DatahotelException e) {
            throw new RemoteDatasourceException(e.getMessage(), e);
        }
    }
}
