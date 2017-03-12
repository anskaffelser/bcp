package no.difi.virksert.server.form;

import no.difi.virksert.server.domain.User;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author erlend
 */
public class UserForm {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

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

    public User update(User user) {
        user.setName(getName());
        user.setEmail(getEmail());
        return user;
    }
}
