package no.difi.virksert.server.form;

/**
 * @author erlend
 */
public class SigninCodeForm {

    private String participant;

    public SigninCodeForm() {
    }

    public SigninCodeForm(String participant) {
        this.participant = participant;
    }

    public String getParticipant() {
        return participant;
    }

    public void setParticipant(String participant) {
        this.participant = participant;
    }
}
