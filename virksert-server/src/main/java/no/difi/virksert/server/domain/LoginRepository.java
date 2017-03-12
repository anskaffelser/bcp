package no.difi.virksert.server.domain;

import org.springframework.data.repository.CrudRepository;

/**
 * @author erlend
 */
public interface LoginRepository extends CrudRepository<Login, Long> {

    Login findByParticipantAndCode(Participant participant, String code);

}
