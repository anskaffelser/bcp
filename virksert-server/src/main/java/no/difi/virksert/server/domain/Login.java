package no.difi.virksert.server.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author erlend
 */
@Entity
public class Login implements Serializable {

    private static final long serialVersionUID = 8768810755862952584L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    private Participant participant;

    @Enumerated(EnumType.STRING)
    private Type remoteType;

    private long remoteId;

    private String code;

    private Date timestamp;

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

    public Type getRemoteType() {
        return remoteType;
    }

    public void setRemoteType(Type remoteType) {
        this.remoteType = remoteType;
    }

    public long getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(long remoteId) {
        this.remoteId = remoteId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Login{" +
                "id=" + id +
                ", remoteType=" + remoteType +
                ", remoteId=" + remoteId +
                ", code='" + code + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    public enum Type {
        USER,
        CERTIFICATE
    }
}
