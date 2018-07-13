package org.nipu.jmt.transaction;

import org.junit.Test;
import org.nipu.jmt.account.Account;
import org.nipu.jmt.account.Accounts;
import org.nipu.jmt.account.InMemoryAccounts;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Test for {@link BrokerProcess}.
 *
 * @author Nikita_Puzankov
 */
public class BrokerProcessTest {
    private final int capacity = 100;
    private final long amount = 100L;
    private BigDecimal overdraft = BigDecimal.valueOf(capacity * amount);

    @Test(timeout = 10000L)
    public void testConcurrentTransactionsProcessingFromCustomer1toCustomer2() throws InterruptedException {
        final Accounts accounts = new InMemoryAccounts();
        final Account customer1 = accounts.add(new Account("customer1", overdraft));
        final Account customer2 = accounts.add(new Account("customer2", overdraft));
        final TransactionsQueue transactionsQueue = new TransactionsQueue(capacity);
        final BrokerProcess brokerProcess = new BrokerProcess(transactionsQueue, accounts);
        final ExecutorService executorService = Executors.newFixedThreadPool(10);
        executorService.submit(brokerProcess);
        for (int i = 0; i < capacity; i++) {
            executorService.submit(
                    () -> {
                        transactionsQueue.offer(
                                new Transaction(1L, 2L, BigDecimal.valueOf(amount))
                        );
                    }
            );
        }
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
        assertEquals(BigDecimal.valueOf(100L * 100), customer2.getBalance());
        assertEquals(BigDecimal.valueOf(-100L * 100), customer1.getBalance());
    }

    @Test(timeout = 10000L)
    public void testConcurrentTransactionsProcessingBidirectional() throws InterruptedException {
        final Accounts accounts = new InMemoryAccounts();
        final Account customer1 = accounts.add(new Account("customer1", overdraft));
        final Account customer2 = accounts.add(new Account("customer2", overdraft));
        final TransactionsQueue transactionsQueue = new TransactionsQueue(capacity);
        final BrokerProcess brokerProcess = new BrokerProcess(transactionsQueue, accounts);
        final ExecutorService executorService = Executors.newFixedThreadPool(10);
        executorService.submit(brokerProcess);
        for (int i = 0; i < capacity / 2; i++) {
            executorService.submit(
                    () -> {
                        transactionsQueue.offer(
                                new Transaction(1L, 2L, BigDecimal.valueOf(amount))
                        );
                    }
            );
        }
        for (int i = 0; i < capacity / 2; i++) {
            executorService.submit(
                    () -> {
                        transactionsQueue.offer(
                                new Transaction(2L, 1L, BigDecimal.valueOf(amount))
                        );
                    }
            );
        }
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
        assertEquals(BigDecimal.ZERO, customer1.getBalance().add(customer2.getBalance()));
    }


    @Test(timeout = 10000L)
    public void testConcurrentTransactionsProcessingBidirectionalAndChaotic() throws InterruptedException {
        final Accounts accounts = new InMemoryAccounts();
        final Account customer1 = accounts.add(new Account("customer1", overdraft));
        final Account customer2 = accounts.add(new Account("customer2", overdraft));
        final TransactionsQueue transactionsQueue = new TransactionsQueue(100);
        final BrokerProcess brokerProcess = new BrokerProcess(transactionsQueue, accounts);
        final ExecutorService executorService = Executors.newFixedThreadPool(10);
        executorService.submit(brokerProcess);
        for (int i = 0; i < capacity; i++) {
            executorService.submit(
                    () -> {
                        final long fromAccountNumber = System.currentTimeMillis() % 2 == 0 ? 1L : 2L;
                        final long toAccountNumber = fromAccountNumber == 1L ? 2L : 1L;
                        transactionsQueue.offer(
                                new Transaction(fromAccountNumber, toAccountNumber, BigDecimal.valueOf(amount))
                        );
                    }
            );
        }
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
        assertEquals(BigDecimal.ZERO, customer1.getBalance().add(customer2.getBalance()));
    }
}