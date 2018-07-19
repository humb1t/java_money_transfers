package org.nipu.jmt.account;

import org.jetbrains.annotations.NotNull;
import org.nipu.jmt.Result;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BinaryOperator;

/**
 * Account represents customer account in our system.
 * Account stores money and provides API to withdraw or put onto Account.
 *
 * @author Nikita_Puzankov
 */
public class Account {
    private final String customerName;
    private final BigDecimal overdraft;
    private final AtomicReference<BigDecimal> balance;

    public Account(String customerName, BigDecimal overdraft) {
        this.customerName = customerName;
        this.overdraft = overdraft;
        balance = new AtomicReference<>(
                BigDecimal.ZERO
        );
    }

    public synchronized Result<Account, AccountError> withdrawMoney(BigDecimal amount) {
        final BigDecimal futureBalancePlusOverdraft = overdraft.add(balance.get().subtract(amount));
        if (futureBalancePlusOverdraft.compareTo(BigDecimal.ZERO) < 0) {
            return new Result<>(
                    null,
                    new AccountError("Overdraft was reached by " + futureBalancePlusOverdraft.abs()
                    )
            );
        }
        return getAccountAccountErrorResult(amount, BigDecimal::subtract);
    }


    public synchronized Result<Account, AccountError> putMoney(BigDecimal amount) {
        return getAccountAccountErrorResult(amount, BigDecimal::add);
    }

    /**
     * Get current balance.
     *
     * @return {@link BigDecimal} representation of balance. No need to copy - it's immutable.
     */
    public BigDecimal getBalance() {
        return this.balance.get();
    }

    @NotNull
    private Result<Account, AccountError> getAccountAccountErrorResult(BigDecimal amount, BinaryOperator<BigDecimal> operator) {
        Objects.requireNonNull(amount);
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            return new Result<>(
                    null,
                    new AccountError("Negative amount is prohibited.")
            );
        }
        this.balance.accumulateAndGet(amount, operator);
        return new Result<>(
                this,
                null
        );
    }

    @Override
    public String toString() {
        return "Account{" +
                "customerName='" + customerName + '\'' +
                ", balance=" + balance.get() +
                '}';
    }
}
