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

package no.difi.bcp.server.domain;

import no.difi.bcp.api.Role;

/**
 * @author erlend
 */
public class ProcessSet {

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

    public boolean equalsRegistration(Registration registration) {
        return registration.getProcess().equals(process)
                && registration.getRole().equals(role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcessSet that = (ProcessSet) o;

        if (!process.equals(that.process)) return false;
        return role == that.role;
    }

    @Override
    public int hashCode() {
        int result = process.hashCode();
        result = 31 * result + role.hashCode();
        return result;
    }

    public String toString() {
        return String.format("%s/%s", process.toVefa().toString(), role);
    }
}
