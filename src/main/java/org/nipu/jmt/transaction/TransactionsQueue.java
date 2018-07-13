package org.nipu.jmt.transaction;


import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * All in one realization of transactions queue.
 *
 * @author Nikita_Puzankov
 */
public class TransactionsQueue {
    private final Queue<Transaction> origin;

    public TransactionsQueue(int capacity) {
        origin = new ArrayBlockingQueue(capacity);
    }

    public boolean offer(Transaction transaction) {
        assert Objects.nonNull(transaction);
        return this.origin.offer(transaction);
    }

    public Optional<Transaction> poll() {
        return Optional.ofNullable(this.origin.poll());
    }
}
