/*
 * Copyright 2017 Norwegian Agency for Public Management and eGovernment (Difi)
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package no.difi.bcp.server.domain;

import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.Scheme;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(
        indexes = {
                @Index(columnList = "scheme,identifier"),
                @Index(columnList = "parent_id")
        },
        uniqueConstraints = @UniqueConstraint(columnNames = {"scheme", "identifier"})
)
public class Participant implements Serializable {

    private static final long serialVersionUID = -3992726170847867427L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String identifier;

    private String scheme;

    private String name;

    @ManyToOne
    private Participant parent;

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

    public Participant getParent() {
        return parent;
    }

    public void setParent(Participant parent) {
        this.parent = parent;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Participant that = (Participant) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
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
