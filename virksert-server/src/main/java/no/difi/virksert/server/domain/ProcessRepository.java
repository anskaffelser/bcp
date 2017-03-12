package no.difi.virksert.server.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author erlend
 */
public interface ProcessRepository extends CrudRepository<Process, Long> {

    Process findByIdentifierAndScheme(String identifier, String Scheme);

    Page<Process> findAll(Pageable pageable);

    List<Process> findAll();

    @Query("select r.process from Registration r where r.certificate = ?1")
    List<Process> findByCertificate(Certificate certificate);

    @Query("select distinct r.process from Registration r where r.participant = ?1")
    List<Process> findByParticipant(Participant participant);

}
