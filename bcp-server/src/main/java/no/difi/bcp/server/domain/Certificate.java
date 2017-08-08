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

import javax.persistence.*;
import java.io.Serializable;
import java.net.URI;
import java.util.Date;
import java.util.List;

/**
 * @author erlend
 */
@Entity
@Table(
        indexes = {
                @Index(columnList = "expiration,revoked"),
                @Index(columnList = "participant_id"),
        },
        uniqueConstraints = @UniqueConstraint(columnNames = {"serialNumber", "participant_id"})
)
public class Certificate implements Serializable {

    private static final long serialVersionUID = -4468522956082680401L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String identifier;

    private String name;

    private String subject;

    @ManyToOne
    private Issuer issuer;

    @ManyToOne
    private Participant participant;

    @OneToMany
    private List<Registration> registrations;

    @Lob
    private byte[] certificate;

    private String serialNumber;

    private URI ocspUri;

    private long expiration;

    private Long revoked;

    private long validated;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Issuer getIssuer() {
        return issuer;
    }

    public void setIssuer(Issuer issuer) {
        this.issuer = issuer;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public List<Registration> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(List<Registration> registrations) {
        this.registrations = registrations;
    }

    public byte[] getCertificate() {
        return certificate;
    }

    public void setCertificate(byte[] certificate) {
        this.certificate = certificate;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public URI getOcspUri() {
        return ocspUri;
    }

    public void setOcspUri(URI ocspUri) {
        this.ocspUri = ocspUri;
    }

    public long getExpiration() {
        return expiration;
    }

    public Date getExpirationDate() {
        return new Date(expiration);
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public Long getRevoked() {
        return revoked;
    }

    public void setRevoked(Long revoked) {
        this.revoked = revoked;
    }

    public long getValidated() {
        return validated;
    }

    public void setValidated(long validated) {
        this.validated = validated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Certificate that = (Certificate) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
