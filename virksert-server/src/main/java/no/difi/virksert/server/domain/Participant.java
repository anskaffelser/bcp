package no.difi.virksert.server.domain;

import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.Scheme;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(
        indexes = {
                @Index(columnList = "scheme,identifier")
        },
        uniqueConstraints = @UniqueConstraint(columnNames = {"scheme", "identifier"})
)
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String identifier;

    private String scheme;

    private String name;

    @OneToMany
    private List<Certificate> certificates;

    @OneToMany
    private List<Registration> registrations;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Certificate> getCertificates() {
        return certificates;
    }

    public void setCertificates(List<Certificate> certificates) {
        this.certificates = certificates;
    }

    public List<Registration> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(List<Registration> registrations) {
        this.registrations = registrations;
    }

    public ParticipantIdentifier toVefa() {
        return ParticipantIdentifier.of(identifier, Scheme.of(scheme));
    }

    @Override
    public String toString() {
        return "Participant{" +
                "id=" + id +
                ", identifier='" + identifier + '\'' +
                ", scheme='" + scheme + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
