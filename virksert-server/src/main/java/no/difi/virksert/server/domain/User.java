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

package no.difi.virksert.server.domain;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author erlend
 */
@Entity
@Table(
        indexes = {
                @Index(columnList = "participant_id")
        },
        uniqueConstraints = @UniqueConstraint(columnNames = {"participant_id", "email"})
)
public class User implements Serializable {

    private static final long serialVersionUID = -2405895317333330988L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    private Participant participant;

    private String name;

    private String email;

    @ColumnDefault("true")
    private boolean hasAccess;

    @ColumnDefault("true")
    private boolean hasReceiveMessages;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isHasAccess() {
        return hasAccess;
    }

    public void setHasAccess(boolean hasAccess) {
        this.hasAccess = hasAccess;
    }

    public boolean isHasReceiveMessages() {
        return hasReceiveMessages;
    }

    public void setHasReceiveMessages(boolean hasReceiveMessages) {
        this.hasReceiveMessages = hasReceiveMessages;
    }

    @Override
    public String toString() {
        return name;
    }
}
