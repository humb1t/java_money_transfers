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
    private final TransactionsQueue queue;
    private final Accounts accounts;
    private final AtomicBoolean run;
    private final Logger log = LoggerFactory.getLogger(BrokerProcess.class);

    public BrokerProcess(TransactionsQueue transactionsQueue, Accounts accounts) {
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
                        log.info("Transaction received {}.", transaction);
                        final Result<Account, AccountError> withdrawResult = accounts.find(transaction.getFromAccountNumber()).withdrawMoney(transaction.getAmount());
                        withdrawResult.getError().ifPresent(
                                accountError -> log.warn(accountError.getMessage())
                        );
                        if (withdrawResult.getError().isPresent()) {
                            return;
                        }
                        log.info("Withdraw result {}", withdrawResult);
                        final Result<Account, AccountError> putResult = accounts.find(transaction.getToAccountNumber()).putMoney(transaction.getAmount());
                        putResult.getError().ifPresent(
                                accountError -> {
                                    accounts.find(transaction.getFromAccountNumber()).putMoney(transaction.getAmount());
                                    log.warn(accountError.getMessage());
                                }
                        );
                        log.info("Put result {}", putResult);
                    }
            );
        }
    }

    public void disable() {
        this.run.set(false);
    }
}
