package no.difi.virksert.server.service;

import no.difi.virksert.server.domain.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author erlend
 */
@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

}
