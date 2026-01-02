package it.younify.modules.sgac.adapter.PIVAlidator;

import it.younify.core.exception.BadRequestException;
import it.younify.core.exception.ResourceNotFoundException;

/**
 * The {@code PIVAlidator} interface defines a contract for validating a
 * "Partita IVA" (VAT number) and retrieving the associated certified email address (PEC).
 * <p>
 * Implementations of this interface will handle the logic of communicating with
 * external services to perform the validation.
 */
public interface PIVAlidator {

    /**
     * Validates a given "Partita IVA" and returns the corresponding PEC.
     *
     * @param PIVA The "Partita IVA" to validate.
     * @return The certified email address (PEC) associated with the given "Partita IVA".
     *         Throws an {@link BadRequestException} if the PIVA is not found or {@link ResourceNotFoundException} if validation fails.
     */
    String PIVAlidatePEC(String PIVA);
}
