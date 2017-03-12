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

package no.difi.virksert.server.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author erlend
 */
public interface ProcessRepository extends CrudRepository<Process, Long> {

    Process findByIdentifierAndScheme(String identifier, String Scheme);

    Page<Process> findAll(Pageable pageable);

    List<Process> findAll();

    @Query("select r.process from Registration r where r.certificate = ?1")
    List<Process> findByCertificate(Certificate certificate);

    @Query("select distinct r.process from Registration r where r.participant = ?1")
    List<Process> findByParticipant(Participant participant);

}
