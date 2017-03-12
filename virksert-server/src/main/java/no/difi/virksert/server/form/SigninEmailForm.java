package no.difi.virksert.server.form;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author erlend
 */
public class SigninEmailForm {

    @NotBlank
    private String participant;

    @NotBlank
    @Email
    private String email;

    public String getParticipant() {
        return participant;
    }

    public void setParticipant(String participant) {
        this.participant = participant;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
