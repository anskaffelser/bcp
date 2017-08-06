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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author erlend
 */
@Repository
public interface CertificateRepository extends CrudRepository<Certificate, Long> {

    List<Certificate> findByParticipant(Participant participant);

    Page<Certificate> findByParticipant(Participant participant, Pageable pageable);

    Certificate findByIdentifier(String identifier);

    @Query("select c from Certificate c where c.ocspUri != null")
    Stream<Certificate> findOcspEnabled();

    @Modifying
    @Query("update Certificate c set c.validated = ?1, c.revoked = null where c in (?2)")
    void updateVerifiedValid(long date, List<Certificate> certificates);

    @Modifying
    @Query("update Certificate c set c.validated = ?1, c.revoked = ?1 where c in (?2)")
    void updateVerifiedInvalid(long date, List<Certificate> certificates);

}
