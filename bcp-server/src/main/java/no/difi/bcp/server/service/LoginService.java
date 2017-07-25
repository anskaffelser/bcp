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

import no.difi.bcp.server.domain.Login;
import no.difi.bcp.server.domain.LoginRepository;
import no.difi.bcp.server.domain.Participant;
import no.difi.bcp.server.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.UUID;

/**
 * @author erlend
 */
@Service
public class LoginService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public void prepare(Participant participant, String email) {
        User user = userService.findUser(participant, email);

        if (user != null) {
            Login login = new Login();
            login.setCode(UUID.randomUUID().toString());
            login.setUser(user);
            login.setTimestamp(new Date());
            login.setParticipant(participant);

            loginRepository.deleteByUser(user);
            loginRepository.save(login);

            emailService.send(email, "OTP for Business Certificate Publisher",
                    String.format("Your code (OTP) is: %s", login.getCode()));
        }
    }

    @Transactional
    public User redeem(Participant participant, String code) {
        Login login = loginRepository.findByParticipantAndCode(participant, code);

        if (login == null)
            return null;
        else {
            loginRepository.delete(login);
            return login.getUser();
        }
    }
}
