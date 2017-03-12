package no.difi.virksert.server.form;

import no.difi.virksert.server.domain.Process;

/**
 * @author erlend
 */
public class ProcessForm {

    private boolean exists;

    private String identifier;

    private String scheme;

    private String title;

    public ProcessForm() {
        exists = false;
    }

    public ProcessForm(Process process) {
        exists = true;

        setIdentifier(process.getIdentifier());
        setScheme(process.getScheme());
        setTitle(process.getTitle());
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Process update(Process process) {
        process.setIdentifier(getIdentifier());
        process.setScheme(getScheme());
        process.setTitle(getTitle());

        return process;
    }
}
