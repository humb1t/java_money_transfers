package org.nipu.jmt.account;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * InMemory realization of {@link Accounts} interface.
 *
 * @author Nikita_Puzankov
 */
public class InMemoryAccounts implements Accounts {

    private final ConcurrentHashMap<Long, Account> accounts;
    private final AtomicLong idCounter;

    public InMemoryAccounts() {
        accounts = new ConcurrentHashMap<>();
        idCounter = new AtomicLong(0L);
    }

    @Override
    public Account add(Account account) {
        final long id = idCounter.incrementAndGet();
        this.accounts.putIfAbsent(
                id,
                account
        );
        return this.accounts.get(id);
    }

    @Override
    //TODO: make this API null safe
    public Account find(Long id) {
        return this.accounts.get(id);
    }

    @Override
    public List<Account> findByName(String name) {
        return this.accounts.values().stream().filter(account -> name.equalsIgnoreCase(name)).collect(Collectors.toList());
    }

    @Override
    public List<Account> findAll() {
        return new ArrayList<>(this.accounts.values());
    }
}
