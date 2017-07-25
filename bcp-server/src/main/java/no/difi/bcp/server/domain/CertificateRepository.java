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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author erlend
 */
@Repository
public interface CertificateRepository extends CrudRepository<Certificate, Long> {

    Page<Certificate> findByParticipant(Participant participant, Pageable pageable);

    Certificate findByParticipantAndIdentifier(Participant participant, String identifier);

    int countByParticipant(Participant participant);

    @Query("select r.certificate from Registration r where r.participant = ?1 and r.process = ?2 and r.role = ?3")
    List<Certificate> findByParticipantAndProcessAndRole(Participant participant, Process process, Role role);

}
