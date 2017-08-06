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

package no.difi.bcp.server.service;

import no.difi.bcp.server.domain.Participant;
import no.difi.bcp.server.domain.User;
import no.difi.bcp.server.domain.UserRepository;
import no.difi.bcp.server.lang.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * @author erlend
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public User findById(long id) {
        return userRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public List<User> findByParticipant(Participant participant) {
        return userRepository.findByParticipant(participant);
    }

    @Transactional
    public User findUser(Participant participant, String email) {
        User user = userRepository.findByParticipantAndEmailIgnoreCase(participant, email);

        if (user == null && participant == null && userRepository.countByParticipant(null) == 0) {
            user = new User();
            user.setName("Admin");
            user.setEmail(email);
            save(user);
        }

        return user;
    }

    @Transactional(readOnly = true)
    public User findUserByIdentifier(Participant participant, String identifier) throws UserNotFoundException {
        User user = userRepository.findByParticipantAndIdentifier(participant, identifier);

        if (user == null)
            throw new UserNotFoundException();

        return user;
    }

    @Transactional
    public void save(User user) {
        if (user.getIdentifier() == null)
            user.setIdentifier(UUID.randomUUID().toString());

        userRepository.save(user);
    }

    @Transactional
    public void delete(User user) {
        userRepository.delete(user);
    }
}
