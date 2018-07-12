package org.nipu.jmt.account;

import java.util.List;

/**
 * {@link Accounts} decorator. Provides thread-safety (by synchronized) to origin {@link Accounts}.
 *
 * @author Nikita_Puzankov
 */
public class SyncAccounts implements Accounts {
    private final Accounts origin;

    public SyncAccounts(Accounts origin) {
        this.origin = origin;
    }

    @Override
    public synchronized Account add(Account account) {
        return origin.add(account);
    }

    @Override
    public synchronized Account find(Long id) {
        return origin.find(id);
    }

    @Override
    public synchronized List<Account> findByName(String name) {
        return origin.findByName(name);
    }

    @Override
    public synchronized List<Account> findAll() {
        return origin.findAll();
    }
}
