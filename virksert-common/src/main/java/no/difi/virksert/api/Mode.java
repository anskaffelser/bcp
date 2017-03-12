package no.difi.virksert.api;

import java.util.Optional;

/**
 * Defines validation modes available as part of this package.
 *
 * @author erlend
 */
public enum Mode {

    @RecipePath("/pki/recipe-norway-test.xml")
    TEST,

    @RecipePath("/pki/recipe-norway-production.xml")
    PRODUCTION;

    /**
     * Fetches {@link Mode} by comparing name using String#equalsIgnoreCase.
     *
     * @param value Some string.
     * @return Mode if found.
     */
    public static Optional<Mode> of(String value) {
        for (Mode mode : values())
            if (mode.name().equalsIgnoreCase(value))
                return Optional.of(mode);

        return Optional.empty();
    }
}
