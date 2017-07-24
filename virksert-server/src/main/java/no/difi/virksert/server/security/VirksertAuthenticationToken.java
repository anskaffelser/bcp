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

package no.difi.virksert.server.security;

import no.difi.virksert.server.domain.Participant;
import no.difi.virksert.server.domain.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author erlend
 */
public class VirksertAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private static final long serialVersionUID = -5240599738400104675L;

    public static VirksertAuthenticationToken newInstance(User user) {
        return new VirksertAuthenticationToken(user, createGrantedAuthorities(user, user.getParticipant()));
    }

    public VirksertAuthenticationToken(User user, Collection<? extends GrantedAuthority> authorities) {
        super(user, new Date(), authorities);
    }

    @Override
    public User getPrincipal() {
        return (User) super.getPrincipal();
    }

    protected static List<GrantedAuthority> createGrantedAuthorities(User user, Participant participant) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (participant == null)
            authorities.add(new SimpleGrantedAuthority("ADMIN"));
        else {
            authorities.add(new SimpleGrantedAuthority("USER"));
            authorities.add(new SimpleGrantedAuthority(participant.toVefa().toString()));
        }

        if (user instanceof MasqueradeUser)
            authorities.add(new SimpleGrantedAuthority("MASQUERADE"));

        return authorities;
    }
}
