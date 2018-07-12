package org.nipu.jmt.account;

import org.nipu.jmt.Result;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Account represents customer account in our system.
 * Account stores money and provides API to withdraw or put onto Account.
 *
 * @author Nikita_Puzankov
 */
public class Account {
    //TODO: provide additional properties (number, customer name, etc.)
    private final AtomicReference<BigDecimal> balance;

    public Account() {
        balance = new AtomicReference<>(
                BigDecimal.ZERO
        );
    }

    public Result<Account, AccountError> withdrawMoney(BigDecimal amount) {
        Objects.nonNull(amount);
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            return new Result<>(
                    null,
                    new AccountError("Negative amount is prohibited.")
            );
        }
        this.balance.accumulateAndGet(amount, BigDecimal::subtract);
        //TODO: check overdraft
        return new Result<>(
                this,
                null
        );
    }

    public Result<Account, AccountError> putMoney(BigDecimal amount) {
        Objects.nonNull(amount);
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            return new Result<>(
                    null,
                    new AccountError("Negative amount is prohibited.")
            );
        }
        this.balance.accumulateAndGet(amount, BigDecimal::add);
        //TODO: check overdraft
        return new Result<>(
                this,
                null
        );
    }
}
