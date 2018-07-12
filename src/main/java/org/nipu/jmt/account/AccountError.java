package org.nipu.jmt.account;

/**
 * Exception related to operations with Account.
 *
 * @author Nikita_Puzankov
 */
public class AccountError {

    private final String message;

    public AccountError(String message) {
        this.message = message;
    }
}
