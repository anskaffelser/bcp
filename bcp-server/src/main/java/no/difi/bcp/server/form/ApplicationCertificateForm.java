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

package no.difi.bcp.server.form;

import no.difi.bcp.server.domain.Application;
import no.difi.bcp.server.domain.Certificate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author erlend
 */
public class ApplicationCertificateForm {

    private List<Certificate> certificates;

    public ApplicationCertificateForm() {
        this.certificates = new ArrayList<>();
    }

    public ApplicationCertificateForm(List<Certificate> certificates) {
        this.certificates = certificates;
    }

    public List<Certificate> getCertificates() {
        return certificates;
    }

    public void setCertificates(List<Certificate> certificates) {
        this.certificates = certificates;
    }

    public Application update(Application application) {
        application.setCertificates(getCertificates().stream()
                .filter(c -> c.getParticipant().equals(application.getParticipant()))
                .collect(Collectors.toList()));

        return application;
    }
}
