package org.nipu.jmt.account;

import java.util.List;

/**
 * {@link Accounts} decorator. Provides REST API based on origin {@link Accounts}.
 *
 * @author Nikita_Puzankov
 */
public class RestAccounts implements Accounts {
    private final Accounts origin;

    public RestAccounts(Accounts origin) {
        this.origin = origin;
    }

    @Override
    public Account add(Account account) {
        return origin.add(account);
    }

    @Override
    public Account find(Long id) {
        return origin.find(id);
    }

    @Override
    public List<Account> findByName(String name) {
        return origin.findByName(name);
    }

    @Override
    public List<Account> findAll() {
        return origin.findAll();
    }
}
