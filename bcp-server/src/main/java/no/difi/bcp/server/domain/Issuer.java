/*
 *  Copyright 2017 Norwegian Agency for Public Management and eGovernment (Difi)
 *
 *  Licensed under the EUPL, Version 1.1 or – as soon they
 *  will be approved by the European Commission - subsequent
 *  versions of the EUPL (the "Licence");
 *
 *  You may not use this work except in compliance with the Licence.
 *
 *  You may obtain a copy of the Licence at:
 *
 *  https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 *  Unless required by applicable law or agreed to in
 *  writing, software distributed under the Licence is
 *  distributed on an "AS IS" basis,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *  express or implied.
 *  See the Licence for the specific language governing
 *  permissions and limitations under the Licence.
 */

package no.difi.bcp.server.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author erlend
 */
@Entity
@Table(
        indexes = {
                @Index(columnList = "identifier")
        },
        uniqueConstraints = @UniqueConstraint(columnNames = {"serialNumber", "subject"})
)
public class Issuer implements Serializable {

    private static final long serialVersionUID = 8702075612455017613L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String identifier;

    private String serialNumber;

    private String subject;

    @Lob
    private byte[] nameHash;

    @Lob
    private byte[] keyHash;

    @Lob
    private byte[] certificate;

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

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public byte[] getNameHash() {
        return nameHash;
    }

    public void setNameHash(byte[] nameHash) {
        this.nameHash = nameHash;
    }

    public byte[] getKeyHash() {
        return keyHash;
    }

    public void setKeyHash(byte[] keyHash) {
        this.keyHash = keyHash;
    }

    public byte[] getCertificate() {
        return certificate;
    }

    public void setCertificate(byte[] certificate) {
        this.certificate = certificate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Issuer issuer = (Issuer) o;

        return id == issuer.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
