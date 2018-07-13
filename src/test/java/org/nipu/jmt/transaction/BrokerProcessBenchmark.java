package org.nipu.jmt.transaction;

import org.nipu.jmt.account.Account;
import org.nipu.jmt.account.Accounts;
import org.nipu.jmt.account.InMemoryAccounts;
import org.openjdk.jmh.annotations.*;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * Simple benchmark to run critical section under heavy load.
 *
 * @author Nikita_Puzankov
 */
public class BrokerProcessBenchmark {

    @Benchmark
    @Fork(value = 4, warmups = 1)
    @Measurement(iterations = 10)
    @Warmup(iterations = 1)
    @Timeout(time = 5)
    @BenchmarkMode(Mode.Throughput)
    public void testConcurrentTransactionsProcessingBidirectionalAndChaotic() throws InterruptedException {
        final int capacity = 5;
        final long amount = 100L;
        final Accounts accounts = new InMemoryAccounts();
        final BigDecimal overdraft = BigDecimal.valueOf(capacity * amount);
        final Account customer1 = accounts.add(new Account("customer1", overdraft));
        final Account customer2 = accounts.add(new Account("customer2", overdraft));
        final TransactionsQueue transactionsQueue = new TransactionsQueue(capacity);
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
    }
}