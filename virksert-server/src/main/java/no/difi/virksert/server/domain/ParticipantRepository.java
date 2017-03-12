package no.difi.virksert.server.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

/**
 * @author erlend
 */
public interface ParticipantRepository extends CrudRepository<Participant, Long> {

    Participant findByIdentifierAndScheme(String identifier, String Scheme);

    Page<Participant> findAll(Pageable pageable);

}
