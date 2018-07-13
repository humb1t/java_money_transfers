package org.nipu.jmt.transaction;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link TransactionsQueue}.
 *
 * @author Nikita_Puzankov
 */
public class TransactionsQueueTest {

    private int capacity = 100;
    private Long fromAccountNumber = 1L;
    private Long toAccountNumber = 2L;
    private BigDecimal amount = BigDecimal.valueOf(100L);

    @Test
    public void offer() {
        TransactionsQueue queue = new TransactionsQueue(capacity);
        assertTrue(queue.offer(new Transaction(fromAccountNumber, toAccountNumber, amount)));
    }

    @Test
    public void offerFullQueue() {
        TransactionsQueue queue = new TransactionsQueue(1);
        assertTrue(queue.offer(new Transaction(fromAccountNumber, toAccountNumber, amount)));
        assertFalse(queue.offer(new Transaction(fromAccountNumber, toAccountNumber, amount)));
    }

    @Test
    public void offer100Transactions() {
        TransactionsQueue queue = new TransactionsQueue(capacity);
        for (int i = 0; i < 100; i++) {
            assertTrue(queue.offer(new Transaction(fromAccountNumber, toAccountNumber, amount)));
        }
    }

    @Test
    public void offer100TransactionsInParallel() throws InterruptedException, ExecutionException {
        TransactionsQueue queue = new TransactionsQueue(capacity);
        final ExecutorService executorService = Executors.newFixedThreadPool(10);
        final List<Future<Boolean>> futures = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            futures.add(
                    executorService.submit(
                            () -> {
                                System.out.println(Thread.currentThread().getId());
                                return queue.offer(new Transaction(fromAccountNumber, toAccountNumber, amount));
                            }
                    )
            );
        }
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
        for (Future<Boolean> future : futures) {
            assertTrue(future.isDone());
            assertTrue(future.get());
        }
    }

    @Test
    public void poll() {
        TransactionsQueue queue = new TransactionsQueue(capacity);
        queue.offer(new Transaction(fromAccountNumber, toAccountNumber, amount));
        assertTrue(queue.poll().isPresent());
    }

    @Test
    public void pollEmptyQueue() {
        TransactionsQueue queue = new TransactionsQueue(capacity);
        assertFalse(queue.poll().isPresent());
    }

    @Test
    public void poll100TransactionsInParallel() throws InterruptedException, ExecutionException {
        TransactionsQueue queue = new TransactionsQueue(capacity);
        for (int i = 0; i < 100; i++) {
            assertTrue(queue.offer(new Transaction(fromAccountNumber, toAccountNumber, amount)));
        }
        final ExecutorService executorService = Executors.newFixedThreadPool(10);
        final List<Future<Optional<Transaction>>> futures = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            futures.add(
                    executorService.submit(
                            () -> {
                                System.out.println(Thread.currentThread().getId());
                                return queue.poll();
                            }
                    )
            );
        }
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
        for (Future<Optional<Transaction>> future : futures) {
            assertTrue(future.isDone());
            assertTrue(future.get().isPresent());
        }
    }
}