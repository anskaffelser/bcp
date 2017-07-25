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

package no.difi.bcp.server.security;

import no.difi.bcp.server.domain.Participant;
import no.difi.bcp.server.domain.User;

import java.io.Serializable;

/**
 * @author erlend
 */
public class MasqueradeUser extends User implements Serializable {

    private static final long serialVersionUID = 3574150706753204487L;

    private User user;

    private Participant participant;

    public MasqueradeUser(User user, Participant participant) {
        this.user = user;
        this.participant = participant;
    }

    public User getOriginal() {
        return user;
    }

    @Override
    public long getId() {
        return user.getId();
    }

    @Override
    public String getIdentifier() {
        return user.getIdentifier();
    }

    @Override
    public Participant getParticipant() {
        return participant;
    }

    @Override
    public String getName() {
        return user.getName();
    }

    @Override
    public String getEmail() {
        return user.getEmail();
    }
}
