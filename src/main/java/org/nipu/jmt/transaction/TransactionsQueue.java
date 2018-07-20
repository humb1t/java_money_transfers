package org.nipu.jmt.transaction;


import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * All in one realization of transactions queue. {@link ArrayBlockingQueue} provides simple synchronization of
 * work with transactions. We are luck of parallel transactions execution to be sure that they are processed in right order.
 *
 * @author Nikita_Puzankov
 */
public class TransactionsQueue {
    private final Queue<Transaction> origin;

    public TransactionsQueue(int capacity) {
        origin = new ArrayBlockingQueue<>(capacity);
    }

    public TransactionsQueue(Queue<Transaction> queueImpl) {
        this.origin = queueImpl;
    }

    public boolean offer(Transaction transaction) {
        Objects.requireNonNull(transaction);
        return this.origin.offer(transaction);
    }

    public Optional<Transaction> poll() {
        return Optional.ofNullable(this.origin.poll());
    }
}
