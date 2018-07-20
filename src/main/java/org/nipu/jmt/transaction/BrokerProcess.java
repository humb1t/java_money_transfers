package org.nipu.jmt.transaction;

import org.nipu.jmt.Result;
import org.nipu.jmt.account.Account;
import org.nipu.jmt.account.AccountError;
import org.nipu.jmt.account.Accounts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Simple transactions processing.
 * <p>
 * Dedicated single execution thread is mandatory. Not ready for parallel execution.
 *
 * @author Nikita_Puzankov
 */
public class BrokerProcess implements Runnable {
    /**
     * Name would be used to debug process in multi broker scenario.
     */
    private final String name;
    private final TransactionsQueue queue;
    private final Accounts accounts;
    private final AtomicBoolean run;
    private final Logger log = LoggerFactory.getLogger(BrokerProcess.class);

    public BrokerProcess(String name, TransactionsQueue transactionsQueue, Accounts accounts) {
        this.name = name;
        queue = transactionsQueue;
        this.accounts = accounts;
        run = new AtomicBoolean(true);
    }

    @Override
    public void run() {
        while (run.get()) {
            queue.poll().ifPresent(
                    transaction -> {
                        //IT SHOULD BE ATOMIC OPERATION, ALL OR NOTHING
                        log.info("Transaction received {} by broker {}.", transaction, this.name);
                        final Account fromAccount = accounts.find(transaction.getFromAccountNumber());
                        final Account toAccount = accounts.find(transaction.getToAccountNumber());
                        Boolean isFromAccountLocked = false;
                        Boolean isToAccountLocked = false;
                        try {
                            isFromAccountLocked = fromAccount.getLock().tryLock();
                            isToAccountLocked = toAccount.getLock().tryLock();
                            final Result<Account, AccountError> withdrawResult = fromAccount.withdrawMoney(transaction.getAmount());
                            withdrawResult.getError().ifPresent(
                                    accountError -> log.warn(accountError.getMessage())
                            );
                            if (withdrawResult.getError().isPresent()) {
                                return;
                            }
                            log.info("Withdraw result {}, by broker {}.", withdrawResult, this.name);
                            final Result<Account, AccountError> putResult = toAccount.putMoney(transaction.getAmount());
                            putResult.getError().ifPresent(
                                    accountError -> {
                                        fromAccount.putMoney(transaction.getAmount());
                                        log.warn(accountError.getMessage());
                                    }
                            );
                            log.info("Put result {}, by broker {}.", putResult, this.name);
                        } finally {
                            if (!(isFromAccountLocked && isToAccountLocked)) {
                                if (isFromAccountLocked) {
                                    fromAccount.getLock().unlock();
                                }
                                if (isToAccountLocked) {
                                    toAccount.getLock().unlock();
                                }
                            }
                        }
                    }
            );
        }
    }

    public void disable() {
        this.run.set(false);
    }
}
