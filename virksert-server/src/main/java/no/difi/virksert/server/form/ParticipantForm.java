package no.difi.virksert.server.form;

import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.virksert.server.domain.Participant;
import no.difi.virksert.server.domain.Process;

public class ParticipantForm {

    private boolean exists;

    private String identifier;

    private String scheme = ParticipantIdentifier.DEFAULT_SCHEME.getValue();

    private String name;

    public ParticipantForm() {
        exists = false;
    }

    public ParticipantForm(Participant participant) {
        exists = true;

        setIdentifier(participant.getIdentifier());
        setScheme(participant.getScheme());
        setName(participant.getName());
    }

    public boolean isExists() {
        return exists;
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

    public Participant update(Participant participant) {
        participant.setIdentifier(getIdentifier());
        participant.setScheme(getScheme());
        participant.setName(getName());

        return participant;
    }

}
