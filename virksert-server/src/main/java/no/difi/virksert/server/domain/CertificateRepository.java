package no.difi.virksert.server.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author erlend
 */
public interface CertificateRepository extends CrudRepository<Certificate, Long> {

    Page<Certificate> findByParticipant(Participant participant, Pageable pageable);

    Certificate findByParticipantAndIdentifier(Participant participant, String identifier);

    int countByParticipant(Participant participant);

    @Query("select r.certificate from Registration r where r.participant = ?1 and r.process = ?2")
    List<Certificate> findByParticipantAndProcess(Participant participant, Process process);

}
