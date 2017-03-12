package no.difi.virksert.server.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author erlend
 */
public interface UserRepository extends CrudRepository<User, Long> {

    User findByParticipantAndEmail(Participant participant, String email);

    long countByParticipant(Participant participant);

    List<User> findByParticipant(Participant participant);

}
