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

package no.difi.virksert.server.security;

import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.virksert.server.domain.Participant;
import no.difi.virksert.server.domain.User;
import no.difi.virksert.server.lang.VirksertServerException;
import no.difi.virksert.server.service.LoginService;
import no.difi.virksert.server.service.ParticipantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author erlend
 */
@Component
public class VirksertAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(VirksertAuthenticationProvider.class);

    @Autowired
    private LoginService loginService;

    @Autowired
    private ParticipantService participantService;

    @Override
    public boolean supports(Class<?> aClass) {
        return UsernamePasswordAuthenticationToken.class.equals(aClass);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            Participant participant = null;
            if (!"admin".equals(authentication.getPrincipal()))
                participant = participantService.get(ParticipantIdentifier.of((String) authentication.getPrincipal()));

            Object o = loginService.redeem(participant, (String) authentication.getCredentials());

            if (o instanceof User) {
                List<GrantedAuthority> authorities = new ArrayList<>();

                if (participant == null)
                    authorities.add(new SimpleGrantedAuthority("ADMIN"));
                else {
                    authorities.add(new SimpleGrantedAuthority("USER"));
                    authorities.add(new SimpleGrantedAuthority(participant.toVefa().toString()));
                }

                return new UsernamePasswordAuthenticationToken(o, new Date(), authorities);
            }

            return null;
        } catch (VirksertServerException e) {
            LOGGER.warn(e.getMessage(), e);
            return null;
        }
    }
}
