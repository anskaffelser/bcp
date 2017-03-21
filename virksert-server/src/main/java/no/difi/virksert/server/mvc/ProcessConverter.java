package no.difi.virksert.server.mvc;

import no.difi.vefa.peppol.common.lang.PeppolParsingException;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import no.difi.virksert.server.domain.Process;
import no.difi.virksert.server.lang.ProcessNotFoundException;
import no.difi.virksert.server.service.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * @author erlend
 */
@Component
public class ProcessConverter implements Converter<String, Process> {

    @Autowired
    private ProcessService processService;

    @Override
    public Process convert(String s) {
        try {
            return processService.get(ProcessIdentifier.parse(s));
        } catch (PeppolParsingException | ProcessNotFoundException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }
}
