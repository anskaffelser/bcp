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
