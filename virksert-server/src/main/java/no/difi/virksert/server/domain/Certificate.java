package no.difi.virksert.server.domain;

import javax.persistence.*;
import java.io.Serializable;
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

    private String issuer;

    @ManyToOne
    private Participant participant;

    @OneToMany
    private List<Registration> registrations;

    @Lob
    private byte[] certificate;

    private String serialNumber;

    private long expiration;

    private Long revoked;

    private long updated;

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

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
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

    public long getExpiration() {
        return expiration;
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

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

}
