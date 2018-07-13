package org.nipu.jmt.account;

import java.util.List;
import java.util.Objects;

/**
 * Decorator of {@link Accounts}. Provides validations on parameters and results.
 *
 * @author Nikita_Puzankov
 */
public class ValidAccounts implements Accounts {
    private final Accounts origin;

    public ValidAccounts(Accounts origin) {
        this.origin = origin;
    }

    @Override
    public Account add(Account account) {
        assert Objects.nonNull(account);
        return origin.add(account);
    }

    @Override
    public Account find(Long id) {
        assert Objects.nonNull(id);
        final Account account = origin.find(id);
        assert Objects.nonNull(account);
        return account;
    }

    @Override
    public List<Account> findAll() {
        return origin.findAll();
    }
}
