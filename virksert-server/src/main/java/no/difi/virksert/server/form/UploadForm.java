package no.difi.virksert.server.form;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author erlend
 */
public class UploadForm {

    private String name;

    private MultipartFile file;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
