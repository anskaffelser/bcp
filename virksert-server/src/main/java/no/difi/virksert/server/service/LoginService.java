package no.difi.virksert.server.service;

import no.difi.virksert.server.domain.Login;
import no.difi.virksert.server.domain.LoginRepository;
import no.difi.virksert.server.domain.Participant;
import no.difi.virksert.server.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

/**
 * @author erlend
 */
@Service
public class LoginService {

    private static Logger logger = LoggerFactory.getLogger(LoginService.class);

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private UserService userService;

    public void prepare(Participant participant, String email) {
        User user = userService.findUser(participant, email);

        if (user != null) {
            Login login = new Login();
            login.setCode(UUID.randomUUID().toString());
            login.setRemoteId(user.getId());
            login.setRemoteType(Login.Type.USER);
            login.setTimestamp(new Date());
            login.setParticipant(participant);
            loginRepository.save(login);

            logger.info("{}", login);
            logger.info("Code: {}", login.getCode());
        }
    }

    public Object redeem(Participant participant, String code) {
        Login login = loginRepository.findByParticipantAndCode(participant, code);

        if (login == null)
            return null;
        else {
            loginRepository.delete(login);

            if (login.getRemoteType().equals(Login.Type.USER)) {
                return userService.findById(login.getRemoteId());
            } else {
                return null;
            }
        }
    }
}
