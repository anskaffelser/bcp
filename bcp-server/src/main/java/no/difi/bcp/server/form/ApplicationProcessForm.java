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

import no.difi.bcp.api.Role;
import no.difi.bcp.server.domain.Process;

import java.util.List;

/**
 * @author erlend
 */
public class ApplicationProcessForm {

    private List<ProcessSet> processes;

    public List<ProcessSet> getProcesses() {
        return processes;
    }

    public void setProcesses(List<ProcessSet> processes) {
        this.processes = processes;
    }

    public static class ProcessSet {

        private Process process;

        private Role role;

        public ProcessSet() {
        }

        public ProcessSet(Process process, Role role) {
            this.process = process;
            this.role = role;
        }

        public Process getProcess() {
            return process;
        }

        public void setProcess(Process process) {
            this.process = process;
        }

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }

        public String toString() {
            return String.format("%s/%s", process.toVefa().toString(), role);
        }
    }
}
