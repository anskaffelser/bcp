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

import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import no.difi.vefa.peppol.common.model.Scheme;

import javax.persistence.*;
import java.io.Serializable;
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
public class Process implements Serializable {

    private static final long serialVersionUID = 8359063394761561110L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String identifier;

    private String scheme;

    private String title;

    @ManyToOne
    private Domain domain;

    private Type type = Type.ONE_WAY;

    @OneToMany
    private List<Registration> registrations;


    public Process() {
    }

    public Process(String scheme, String identifier, String title, Domain domain, Type type) {
        this.identifier = identifier;
        this.scheme = scheme;
        this.title = title;
        this.domain = domain;
        this.type = type;
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

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
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

    public static enum Type {
        ONE_WAY,
        TWO_WAY
    }
}
