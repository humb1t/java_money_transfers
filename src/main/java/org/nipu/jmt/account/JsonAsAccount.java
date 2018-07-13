package org.nipu.jmt.account;

import org.nipu.jmt.Result;

import java.math.BigDecimal;

/**
 * Provides an ability to deserialize Json String into account.
 *
 * @author Nikita_Puzankov
 */
public class JsonAsAccount implements Account {
    private Long id;
    private String customerName;
    private AtomicAccount atomicAccount;

    public JsonAsAccount() {
    }

    public JsonAsAccount(AtomicAccount atomicAccount) {
        this.atomicAccount = atomicAccount;
    }

    public JsonAsAccount(Long id, String customerName, AtomicAccount atomicAccount) {
        this.id = id;
        this.customerName = customerName;
        this.atomicAccount = atomicAccount;
    }

    @Override
    public Result<Account, AccountError> withdrawMoney(BigDecimal amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Result<Account, AccountError> putMoney(BigDecimal amount) {
        throw new UnsupportedOperationException();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getBalance() {
        return this.atomicAccount.getBalance();
    }
}
