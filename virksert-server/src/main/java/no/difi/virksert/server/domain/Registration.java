package no.difi.virksert.server.domain;

import javax.persistence.*;

/**
 * @author erlend
 */
@Entity
@Table(
        indexes = {
                @Index(columnList = "participant_id,process_id")
        },
        uniqueConstraints = @UniqueConstraint(columnNames = {"certificate_id", "process_id"})
)
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    private Participant participant;

    @ManyToOne
    private Process process;

    @ManyToOne
    private Certificate certificate;

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

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }
}
