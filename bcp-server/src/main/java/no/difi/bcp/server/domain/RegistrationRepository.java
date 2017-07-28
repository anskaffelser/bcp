/*
 * Copyright 2017 Norwegian Agency for Public Management and eGovernment (Difi)
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package no.difi.bcp.server.domain;

import no.difi.bcp.api.Role;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author erlend
 */
@Repository
public interface RegistrationRepository extends CrudRepository<Registration, Long> {

    List<Registration> findByApplication(Application application, Sort sort);

    @Query("select r from Registration r inner join r.application.customers c where c in (?1)")
    List<Registration> findProcesses(Participant participant);

    @Query("select cert from Registration r inner join r.application.customers c inner join r.application.certificates cert where c in (?1) and r.process = ?2 and r.role = ?3")
    List<Certificate> findCertificates(Participant participant, Process process, Role role);

}
