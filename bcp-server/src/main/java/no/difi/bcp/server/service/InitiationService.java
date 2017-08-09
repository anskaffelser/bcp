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

package no.difi.bcp.server.service;

import no.difi.bcp.server.domain.Domain;
import no.difi.bcp.server.domain.Process;
import no.difi.bcp.server.domain.ProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author erlend
 */
@Service
public class InitiationService {

    @Autowired
    private DomainService domainService;

    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private ProcessService processService;

    @PostConstruct
    public void postConstruct() {
        if (processRepository.count() > 0)
            return;

        Domain domain = domainService.save(Domain.newInstance("Post-Award Procurement"));
        processService.save(new Process("busdox-procid-ubl", "urn:www.cenbii.eu:profile:bii01:ver2.0",
                "Catalogue Only (Profile 01)", domain, Process.Type.ONE_WAY));
        processService.save(new Process("busdox-procid-ubl", "urn:www.cenbii.eu:profile:bii03:ver2.0",
                "Order Only (Profile 03)", domain, Process.Type.ONE_WAY));
        processService.save(new Process("busdox-procid-ubl", "urn:www.cenbii.eu:profile:bii04:ver2.0",
                "Invoice (Profile 04)", domain, Process.Type.ONE_WAY));
        processService.save(new Process("busdox-procid-ubl", "urn:www.cenbii.eu:profile:bii05:ver2.0",
                "Billing (Profile 05)", domain, Process.Type.ONE_WAY));
        processService.save(new Process("busdox-procid-ubl", "urn:www.cenbii.eu:profile:bii28:ver2.0",
                "Ordering (Profile 28)", domain, Process.Type.TWO_WAY));
        processService.save(new Process("busdox-procid-ubl", "urn:www.cenbii.eu:profile:bii30:ver2.0",
                "Despatch Advice (Profile 30)", domain, Process.Type.TWO_WAY));
        processService.save(new Process("busdox-procid-ubl", "urn:www.cenbii.eu:profile:biixx:ver2.0",
                "Credit Note Only (Profile XX)", domain, Process.Type.ONE_WAY));

        domain = domainService.save(Domain.newInstance("Payment"));
        processService.save(new Process("busdox-procid-ubl", "urn:fdc:bits.no:2017:profile:01:1.0",
                "General Credit transfer Initiion (Profile 01)", domain, Process.Type.TWO_WAY));
        processService.save(new Process("busdox-procid-ubl", "urn:fdc:bits.no:2017:profile:02:1.0",
                "Cancelation of General Credit transfer Initiation (Profile 02)", domain, Process.Type.TWO_WAY));
        processService.save(new Process("busdox-procid-ubl", "urn:fdc:bits.no:2017:profile:03:1.0",
                "Salary payments (Profile 03)", domain, Process.Type.TWO_WAY));
        processService.save(new Process("busdox-procid-ubl", "urn:fdc:bits.no:2017:profile:04:1.0",
                "Salary payments with request for cancelation (Profile 04)", domain, Process.Type.TWO_WAY));
        processService.save(new Process("busdox-procid-ubl", "urn:fdc:bits.no:2017:profile:05:1.0",
                "Billing (Profile 05)", domain, Process.Type.ONE_WAY));
        processService.save(new Process("busdox-procid-ubl", "urn:fdc:bits.no:2017:profile:06:1.0",
                "Billing system with Direct Debit (Profile 06)", domain, Process.Type.TWO_WAY));
        processService.save(new Process("busdox-procid-ubl", "urn:fdc:bits.no:2017:profile:07:1.0",
                "Canselation of DirectDebit Initiation (Profile 07)", domain, Process.Type.TWO_WAY));
        processService.save(new Process("busdox-procid-ubl", "urn:fdc:bits.no:2017:profile:08:1.0",
                "Profile 08", domain, Process.Type.TWO_WAY));
        processService.save(new Process("busdox-procid-ubl", "urn:fdc:bits.no:2017:profile:09:1.0",
                "Accounting/General Ledger/cash management (Profile 09)", domain, Process.Type.ONE_WAY));
        processService.save(new Process("busdox-procid-ubl", "urn:fdc:bits.no:2017:profile:10:1.0",
                "Profile 10", domain, Process.Type.ONE_WAY));
        processService.save(new Process("busdox-procid-ubl", "urn:fdc:bits.no:2017:profile:11:1.0",
                "Profile 11", domain, Process.Type.ONE_WAY));
    }
}
