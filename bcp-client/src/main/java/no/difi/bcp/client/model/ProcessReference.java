package no.difi.bcp.client.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import no.difi.bcp.api.Role;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import no.difi.vefa.peppol.common.model.Scheme;

/**
 * @author erlend
 */
@Getter
@EqualsAndHashCode
public class ProcessReference {

    private ProcessIdentifier processIdentifier;

    private Role role;

    public static ProcessReference of(String value, Scheme scheme, Role role) {
        return new ProcessReference(
                ProcessIdentifier.of(value, scheme),
                role
        );
    }

    public ProcessReference(ProcessIdentifier processIdentifier, Role role) {
        this.processIdentifier = processIdentifier;
        this.role = role;
    }

    public Scheme getScheme() {
        return processIdentifier.getScheme();
    }

    public String getIdentifier() {
        return processIdentifier.getIdentifier();
    }
}
