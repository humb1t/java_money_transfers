package org.nipu.jmt.transaction;

import org.nipu.jmt.account.Account;

import java.math.BigDecimal;

/**
 * Main class to represent transaction of money between {@link Account}.
 *
 * @author Nikita_Puzankov
 */
public class Transaction {
    private final Long fromAccountNumber;
    private final Long toAccountNumber;
    private final BigDecimal amount;

    public Transaction(Long fromAccountNumber, Long toAccountNumber, BigDecimal amount) {
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.amount = amount;
    }

    public Long getFromAccountNumber() {
        return fromAccountNumber;
    }

    public Long getToAccountNumber() {
        return toAccountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "fromAccountNumber=" + fromAccountNumber +
                ", toAccountNumber=" + toAccountNumber +
                ", amount=" + amount +
                '}';
    }
}
