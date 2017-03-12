package no.difi.virksert.server.service;

import no.difi.virksert.server.domain.Participant;
import no.difi.virksert.server.domain.User;
import no.difi.virksert.server.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author erlend
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User findById(long id) {
        return userRepository.findOne(id);
    }

    public List<User> findByParticipant(Participant participant) {
        return userRepository.findByParticipant(participant);
    }

    public long count(Participant participant) {
        return userRepository.countByParticipant(participant);
    }

    public User findUser(Participant participant, String email) {
        User user = userRepository.findByParticipantAndEmail(participant, email);

        if (user == null && participant == null && userRepository.countByParticipant(null) == 0) {
            user = new User();
            user.setName("Admin");
            user.setEmail(email);
            user.setHasAccess(true);
            user.setHasReceiveMessages(true);
            userRepository.save(user);
        }

        return user;
    }

    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }
}
