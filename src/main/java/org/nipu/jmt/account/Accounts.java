package org.nipu.jmt.account;

import java.util.List;

/**
 * Main interface for accounts.
 *
 * @author Nikita_Puzankov
 */
public interface Accounts {
    Account add(Account account);

    Account find(Long id);

    List<Account> findAll();
}
