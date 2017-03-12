package no.difi.virksert.server.domain;

import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import no.difi.vefa.peppol.common.model.Scheme;

import javax.persistence.*;
import java.util.List;

/**
 * @author erlend
 */
@Entity
@Table(
        indexes = {
                @Index(columnList = "scheme,identifier")
        },
        uniqueConstraints = @UniqueConstraint(columnNames = {"scheme", "identifier"})
)
public class Process {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String identifier;

    private String scheme;

    private String title;

    @OneToMany
    private List<Registration> registrations;


    public Process() {
    }

    public Process(String scheme, String identifier, String title) {
        this.identifier = identifier;
        this.scheme = scheme;
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ProcessIdentifier toVefa() {
        return ProcessIdentifier.of(identifier, Scheme.of(scheme));
    }

    public List<Registration> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(List<Registration> registrations) {
        this.registrations = registrations;
    }
}
