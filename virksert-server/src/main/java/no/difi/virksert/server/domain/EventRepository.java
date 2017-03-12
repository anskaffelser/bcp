package no.difi.virksert.server.domain;

import org.springframework.data.repository.CrudRepository;

/**
 * @author erlend
 */
public interface EventRepository extends CrudRepository<Event, Long> {
}
